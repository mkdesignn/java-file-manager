package filehandler.project;

import filehandler.project.configuration.AWSProperties;
import filehandler.project.configuration.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.sql.DataSource;

@SpringBootApplication
@EnableConfigurationProperties({File.class, AWSProperties.class})
public class ProjectApplication implements CommandLineRunner{


    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {

        SpringApplication.run(ProjectApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(System.getenv("AWS_KEY"));
    }
}
