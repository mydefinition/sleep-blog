package top.gosleep.blog.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultFileUtil {

    public static <T> T getContent(String fileName) throws IOException {
        Path external = Paths.get(fileName);
        if (!Files.exists(external)) {
            try (InputStream in = new ClassPathResource(fileName).getInputStream()) {
                Files.copy(in, external);
            } catch (IOException e) {
                throw new IOException("Failed to load file: " + fileName, e);
            }
        }
        return new ObjectMapper().readValue(external.toFile(), new TypeReference<>() {
        });
    }
}
