package codes.writeonce.maven.plugin.sfx;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;

@Mojo(name = "generate", defaultPhase = PACKAGE, threadSafe = true)
public class GenerateMojo extends AbstractMojo {

    private static final int CHUNK_SIZE = 4096 * 64;

    private static final int LINE_LENGTH = 76;

    private static final byte[] LINE_SEPARATOR = {0x0A};

    @Parameter(required = true)
    protected File sourceArchive;

    @Parameter
    protected String installerCommand;

    @Parameter
    protected String classifier;

    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(required = true, readonly = true, defaultValue = "${project.build.directory}")
    private File buildDirectory;

    @Parameter(readonly = true, defaultValue = "${project.build.finalName}")
    private String finalName;

    @Component
    private MavenProjectHelper projectHelper;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            process();
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException("Failed to generate SFX: " + e.getMessage(), e);
        }
    }

    protected void process() throws Exception {

        final String artifactFileName = finalName + (classifier == null ? ".sh" : "-" + classifier + ".sh");
        final File artifactFile = new File(buildDirectory, artifactFileName);

        final byte[] buffer = new byte[CHUNK_SIZE];

        try (InputStream inputStream = new FileInputStream(sourceArchive);
             FileOutputStream outputStream = new FileOutputStream(artifactFile);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8)) {
            pump(buffer, outputStream, "header1.sh");
            if (installerCommand != null) {
                writer.write(installerCommand);
                writer.write('\n');
                writer.flush();
                pump(buffer, outputStream, "header2.sh");
            }
            pump(buffer, outputStream, "header3.sh");
            try (Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream, true, LINE_LENGTH,
                    LINE_SEPARATOR)) {
                pump(buffer, inputStream, base64OutputStream);
            }
        }

        if (classifier == null) {
            projectHelper.attachArtifact(project, "sh", artifactFile);
        } else {
            projectHelper.attachArtifact(project, "sh", classifier, artifactFile);
        }
    }

    private void pump(byte[] buffer, OutputStream outputStream, String name) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(name)) {
            pump(buffer, inputStream, outputStream);
        }
    }

    private static void pump(byte[] buffer, InputStream inputStream, OutputStream outputStream) throws IOException {

        while (true) {
            final int length = inputStream.read(buffer);
            if (length == -1) {
                break;
            }
            outputStream.write(buffer, 0, length);
        }
    }
}
