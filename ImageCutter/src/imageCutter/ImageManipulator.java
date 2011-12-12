/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imageCutter;

import com.ice.tar.InvalidHeaderException;
import com.ice.tar.TarEntry;
import com.ice.tar.TarOutputStream;
import com.objectplanet.image.PngEncoder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/**
 *
 * @author MenKat
 */
public class ImageManipulator extends SwingWorker<Void, Void> {

    private PngEncoder encoder;

    private MainFrame main;
    public File imageFile;
    private BufferedImage image;
    public int iWidth;
    public int iHeight;
    public int maxImages;
    public int actualImages;
    private String name;
    private String outPutPath;

    private int tileSizeX;
    private int tileSizeY;
    public boolean compressToTar;
    public String configFilePath;

    public boolean finished;

    public ImageManipulator(File imageFile, MainFrame main) {
        try {
             encoder = new PngEncoder(PngEncoder.COLOR_INDEXED_ALPHA);
             encoder.setCompression(PngEncoder.BEST_COMPRESSION);

            this.main = main;
            this.imageFile = imageFile;
System.out.println("Read image: " + imageFile.getAbsolutePath());
            this.image = ImageIO.read(imageFile);
            this.iWidth = image.getWidth();
            this.iHeight = image.getHeight();
            this.finished = false;

            if (iWidth % 4 != 0 || iHeight % 4 != 0) {
                iWidth = iWidth + (4 - iWidth % 4);
                iHeight = iHeight + (4 - iHeight % 4);
                BufferedImage img = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
                Graphics g = img.getGraphics();
                g.drawImage(this.image, 0, 0, null);
                this.image = img;
            }

        } catch (IOException ex) {
            Logger.getLogger(ImageManipulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setParametres(String name, String outPutPath, int tileSizeX, int tileSizeY) {
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
        this.name = name;
        this.outPutPath = outPutPath;

        this.maxImages = iWidth / tileSizeX * iHeight / tileSizeY;
System.out.println("Name: " + name + " outputPath: " + outPutPath);
    }

    private byte[] getFileAsBytes(File file) {
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            return b;
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return null;
        } catch (IOException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public static String addZerosBefore(int s, int length) {
        String prefix = "";
        for (int i = 0; i < (length - String.valueOf(s).length()); i++) {
            prefix = prefix.concat("0");
        }
        return prefix + s;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        File outPutFile = new File(outPutPath);
        File outPutTempFile = new File(outPutFile.getAbsolutePath() + File.separator + name + File.separator);
        if (!outPutFile.exists()) {
            outPutFile.mkdirs();
        }
        if (!outPutTempFile.exists()) {
            outPutTempFile.mkdirs();
        }

        actualImages = 0;
        setProgress((actualImages * 100) / maxImages);
        for (int i = 0; i < iWidth; i = i + tileSizeX) {
            for (int j = 0; j < iHeight; j = j + tileSizeY) {
                if (isCancelled()) {
                    break;
                }

                actualImages++;
                int progress = Math.min((actualImages * 100) / maxImages, 99);
                setProgress(progress);

                BufferedImage ima = new BufferedImage(tileSizeX, tileSizeY, BufferedImage.TYPE_INT_RGB);
                Graphics g = ima.getGraphics();
                g.setColor(new Color(0xfeddb1));
                g.fillRect(0, 0, tileSizeX, tileSizeY);
                int subImageWidth = (i + tileSizeX) < iWidth ? tileSizeX : iWidth - i;
                int subImageHeight = (j + tileSizeY) < iHeight ? tileSizeY : iHeight - j;

                g.drawImage(image.getSubimage(i, j, subImageWidth, subImageHeight), 0, 0, null);

                try {
                    File fil = new File(outPutTempFile.getAbsolutePath() + File.separator +
                                addZerosBefore((i/tileSizeX), 3) + "_" + addZerosBefore((j/tileSizeY), 3) + ".png");
//System.out.println("New image: " + fil.getAbsolutePath());
                    if (fil.exists()) {
                        fil.delete();
                    }

                    FileOutputStream fos = new FileOutputStream(fil);
                    encoder.encode(ima, fos);

                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageManipulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (main != null)
            main.progressMonitor.setNote("Tar packing...");

        if (compressToTar) {
            try {
                File tarFile = new File(outPutFile.getAbsolutePath() + File.separator + name + ".tar");
                FileOutputStream fos = new FileOutputStream(tarFile);

                TarOutputStream tos = new TarOutputStream(fos);

                TarEntry entry;
                File[] files = outPutTempFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    entry = new TarEntry(files[i]);
                    entry.setName(name + File.separator + files[i].getName());

                    tos.putNextEntry(entry);
                    tos.write(getFileAsBytes(files[i]));
                    tos.closeEntry();
                    files[i].delete();
                }

                if (configFilePath != null) {
                    File configFile = new File(configFilePath);
                    TarEntry configFileEntry = new TarEntry(configFile);
                    configFileEntry.setName(name + configFile.getName().substring(configFile.getName().lastIndexOf('.')));
                    tos.putNextEntry(configFileEntry);
                    tos.write(getFileAsBytes(configFile));
                    tos.closeEntry();
                }

                tos.close();
                fos.close();
                outPutTempFile.delete();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(ImageManipulator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidHeaderException ex) {
                Logger.getLogger(ImageManipulator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ImageManipulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.finished = true;
        return null;
    }

    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        setProgress(100);
    }
}

