package com.aol.micro.server.s3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.util.ExceptionSoftener;

@Component
public class S3Utils {

	private static final InputStream emptyInputStream = new EmptyInputStream();
	
	private final AmazonS3Client client;
	private final TransferManager transferManager;
	private final String tmpDirectory;

	@Autowired
	public S3Utils(AmazonS3Client client, TransferManager transferManager,
			@Value("${s3.tmp.dir:#{null}}") String tmpDirectory) {
		this.client = client;
		this.transferManager = transferManager;
		this.tmpDirectory = tmpDirectory;
	}

	
	/**
	 * Method returns list of all <b>S3ObjectSummary</b> objects, subject to <i>req</i> parameters. 
	 * Multiple S3 calls will be performed if there are more than 1000 elements there
	 * @param req - ListObjectRequest to be used.
	 * @return List of S3ObjectSummary from bucket, 
	 */
	public List<S3ObjectSummary> getAllSummaries(ListObjectsRequest req) {
		List<S3ObjectSummary> result = new ArrayList<>();
		String marker = null;
		ListObjectsRequest req2 = (ListObjectsRequest) req.clone();
		ObjectListing listing;
		do {
			listing = client.listObjects(req2.withMarker(marker));
			marker = listing.getNextMarker();
			result.addAll(listing.getObjectSummaries());
		} while (listing.isTruncated());

		return result;
	}

	/**
	 * Method return stream of S3ObjectSummary objects, subject to <i>req</i> parameters
	 * Method will perform one query for every 1000 elements (current s3 limitation). 
	 * It is lazy, so there would be no unnecesarry calls
	 * @param req - ListObjectRequest to be used.
	 * @param processor - Function that convert S3ObjectSummary to any object
	 * @return ReactiveSeq of converted S3Object summary elements.
	 */
	public <T> ReactiveSeq<T> getSummariesStream(ListObjectsRequest req, Function<S3ObjectSummary, T> processor) {
		return ReactiveSeq.fromIterator(new S3ObjectSummaryIterator(client, req)).map(processor);
	}

	/**
	 * Method delete all <i>objects</i> from <i>bucketName</i> in groups by 1000 elements
	 * @param bucketName
	 * @param objects
	 */
	public void delete(String bucketName, List<KeyVersion> objects) {
		ReactiveSeq.fromList(objects).grouped(1000).forEach(l -> {
			DeleteObjectsRequest req = new DeleteObjectsRequest(bucketName);
			req.setKeys(l.toList());
			client.deleteObjects(req);
		});
	}

	/**
	 * Method returns InputStream from S3Object. Multi-part download is used to get file.
	 * s3.tmp.dir property used to store temporary files. You can specify temporary file name by
	 * using tempFileSupplier object.
	 * @param bucketName 
	 * @param key - 
	 * @param tempFileSupplier - Supplier providing temporary filenames
	 * @return InputStream of 
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public InputStream getInputStream(String bucketName, String key, Supplier<File> tempFileSupplier)
			throws AmazonServiceException, AmazonClientException, InterruptedException, IOException {
		File file = tempFileSupplier.get();
		try {
			Download download = transferManager.download(bucketName, key, file);
			download.waitForCompletion();
			return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
		} finally {
			file.delete();
		}
	}

	/**
	 * Method returns InputStream from S3Object. Multi-part download is used to get file.
	 * s3.tmp.dir property used to store temporary files.
	 * @param bucketName
	 * @param key
	 * @return
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public InputStream getInputStream(String bucketName, String key)
			throws AmazonServiceException, AmazonClientException, InterruptedException, IOException {
		Supplier<File> tempFileSupplier = ExceptionSoftener.softenSupplier(() -> Files
				.createTempFile(FileSystems.getDefault().getPath(tmpDirectory), "micro-s3", "file").toFile());
		return getInputStream(bucketName, key, tempFileSupplier);
	}
	
	/** Provide empty InputStream.
	 * <p>
	 * This implementation can be convenient 
	 * if you need to place some empty value to s3 bucket.
	 *  
	 * @return empty InputStream
	 */
	public static InputStream emptyInputStream() {
		return emptyInputStream;
	}
	

}
