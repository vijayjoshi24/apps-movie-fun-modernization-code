package org.superbiz.moviefun;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.superbiz.moviefun.albumsapi.AlbumsClient;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.blobstore.S3Store;
import org.superbiz.moviefun.moviesapi.MovieServlet;
import org.superbiz.moviefun.moviesapi.MoviesClient;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(MovieServlet movieServlet) {
        return new ServletRegistrationBean(movieServlet, "/moviefun/*");
    }

    @Value("${s3.endpointUrl}") String s3EndpointUrl;
    @Value("${s3.accessKey}") String s3AccessKey;
    @Value("${s3.secretKey}") String s3SecretKey;
    @Value("${s3.bucketName}") String s3BucketName;
    @Value("${movies.url}") String moviesUrl;
    @Value("${albums.url}") String albumsUrl;

    @Bean
    public BlobStore blobStore() {
        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint(s3EndpointUrl);

        return new S3Store(s3Client, s3BucketName);
    }
    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public MoviesClient moviesClient(RestOperations restOperations) {
        return new MoviesClient(moviesUrl, restOperations);
    }

    @Bean
    public AlbumsClient albumsClients(RestOperations restOperations) {
        return new AlbumsClient(albumsUrl, restOperations);
    }
}
