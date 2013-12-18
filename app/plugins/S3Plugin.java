package plugins;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import play.Application;
import play.Logger;
import play.Plugin;
import utils.Utils;

/**
 * AWS S3 plugin for Play! 2. Contains methods to connect to Amazon S3 for file uploads.
 * Taken from https://devcenter.heroku.com/articles/using-amazon-s3-for-file-uploads-with-java-and-play-2.
 *
 * Date: 30/10/13
 * Time: 10:14
 *
 * @author      Sav Balac
 * @version     1.0
 */

public class S3Plugin extends Plugin {

    public static final String AWS_S3_BUCKET = "aws.s3.bucket";
    public static final String AWS_ACCESS_KEY = "aws.access.key";
    public static final String AWS_SECRET_KEY = "aws.secret.key";
    private final Application application;
    public static AmazonS3 amazonS3;
    public static String s3Bucket;


    /**
     * Constructor.
     * @param application The application
     * @return Result
     */
    public S3Plugin(Application application) {
        this.application = application;
    }


    /*
     * Logs into AWS and creates the S3 bucket.
     */
    @Override
    public void onStart() {
        String accessKey = application.configuration().getString(AWS_ACCESS_KEY);
        String secretKey = application.configuration().getString(AWS_SECRET_KEY);
        s3Bucket = application.configuration().getString(AWS_S3_BUCKET);
        if ((accessKey != null) && (secretKey != null)) {
            try {
                AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
                amazonS3 = new AmazonS3Client(awsCredentials);
                amazonS3.createBucket(s3Bucket);
                Logger.info("Using S3 Bucket: " + s3Bucket);
            } catch (Exception ex) {
                // Log an error
                Logger.error("Could not connect to Amazon S3", ex);
            }
        }
    }


    /*
     * Checks the application has AWS access information.
     */
    @Override
    public boolean enabled() {
        return (application.configuration().keys().contains(AWS_ACCESS_KEY) &&
                application.configuration().keys().contains(AWS_SECRET_KEY) &&
                application.configuration().keys().contains(AWS_S3_BUCKET));
    }


}
