package br.gov.pa.sefa.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This Goal opens each JAR file within the final archive and removes the classes compiled in Java 9+.
 * This is designed to solve problems with deployment on incompatible application servers, for example, which include older versions of the bytecode handling library.
*/
@Mojo(name = "java8-runtime-compatibility", defaultPhase = LifecyclePhase.PACKAGE)
public class Java8RuntimeCompatibility extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() {

        String finalFile = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        String backupFile = finalFile + ".backup";

        if (new File(finalFile).renameTo(new File(backupFile))) {


            try (FileInputStream in = new FileInputStream(backupFile);
                 FileOutputStream out = new FileOutputStream(finalFile)
            ) {
                recompacta(in, out);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            getLog().info("file not found: " + finalFile);
        }

    }

    private void recompacta(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        ZipInputStream zin = new ZipInputStream(in);
        ZipOutputStream zout = new ZipOutputStream(out);


        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {


            if (e.getName().endsWith(".jar")) {
                getLog().debug("recompress: " + e.getName());
                zout.putNextEntry(new ZipEntry(e.getName()));
                recompacta(zin, zout);
            } else if (!e.getName().startsWith("META-INF/versions") && !e.getName().startsWith("module-info.class")) {
                getLog().debug("add: " + e.getName());
                zout.putNextEntry(e);
                int len;
                while ((len = zin.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }
                zout.closeEntry();
            } else {
                getLog().debug("remove: " + e.getName());
            }
        }
        zout.finish();


    }
}
