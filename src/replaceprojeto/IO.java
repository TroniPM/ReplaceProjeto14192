package replaceprojeto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author pmdsilva
 * @date 02-mar-2021
 */
public class IO {

    public static String read(String arquivo) {
        String linhas = null;
        try {
            FileInputStream fin = new FileInputStream(arquivo);
            byte[] a = new byte[fin.available()];
            fin.read(a);
            fin.close();
            linhas = new String(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linhas;
    }

    public static void write(String caminho, String content, boolean isAppend) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(caminho);
            fop = new FileOutputStream(file, isAppend);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deletarArquivos() {
        File f = new File("C:/");
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.getAbsolutePath().toLowerCase().endsWith("_replace.txt")) {
                System.out.println(">> " + file.getAbsolutePath());
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
