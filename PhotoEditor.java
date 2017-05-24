package photoeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PhotoEditor {

    private static JFrame frame;
    private static final JMenuItem[] option = new JMenuItem[15];
    private static final JPanel panel = new JPanel();
    private static BufferedImage image;
    private static BufferedImage transformedImage;

    public static void main(String[] args) {
        try {
            //Sets GUI with Vertical Seperator in menu
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PhotoEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] menuOptions = {"Open", "Save As", "Exit", "Restore to Original", "Horizontal Flip", "Vertical Flip", "Gray Scale", "Sepia Tone", "Invert Colour", "Gaussian Blur", "Bulge Effect", "Rotate Orientation", "Red Filter", "Green Filter", "Blue Filter"};
        int[] keycode = {79, 83, 69, 82, 72, 86, 71, 80, 73, 85, 66, 67, 81, 87, 65};
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu optionMenu = new JMenu("Option");
        menuBar.add(fileMenu);
        menuBar.add(optionMenu);
        //Creates Menu Items along with ButtonListeners
        for (int i = 0; i <= 2; i++) {
            option[i] = new JMenuItem(menuOptions[i], KeyEvent.VK_1);
            option[i].addActionListener(new ButtonListener());
            fileMenu.add(option[i]);
        }
        for (int i = 3; i <= 14; i++) {
            option[i] = new JMenuItem(menuOptions[i], KeyEvent.VK_2);
            option[i].addActionListener(new ButtonListener());
            optionMenu.add(option[i]);
        }
        //Adds Horizontal Separator
        fileMenu.insertSeparator(2);
        optionMenu.insertSeparator(1);
        optionMenu.insertSeparator(9);
        //Adds Keyboard Shortcut for each Menu Item
        for (int i = 0; i <= 14; i++) {
            option[i].setAccelerator(KeyStroke.getKeyStroke(keycode[i], ActionEvent.CTRL_MASK));
            if (i != 0 && i != 2) {
                option[i].setEnabled(false);
            }
        }

        frame = new JFrame("Image Editor GUI");
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();

            switch (command) {
                case "Open":
                    //Opens File Chooser Dialog and only allows image files to be selected
                    JFileChooser imageFileChooser = new JFileChooser();
                    imageFileChooser.setFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));
                    imageFileChooser.setAcceptAllFileFilterUsed(false);
                    imageFileChooser.setDialogTitle("Choose Image File");
                    if (imageFileChooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = imageFileChooser.getSelectedFile();
                        try {
                            image = ImageIO.read(selectedFile);
                            transformedImage = ImageIO.read(selectedFile);
                            refreshPanel(image);
                            for (int i = 0; i <= 14; i++) {
                                option[i].setEnabled(true);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(PhotoEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case "Save As":
                    //Opens Save As Dialog and allows user to choose type of image file to be saved as
                    JFileChooser saveFile = new JFileChooser();
                    saveFile.setDialogTitle("Save As");
                    saveFile.setAcceptAllFileFilterUsed(false);
                    saveFile.addChoosableFileFilter(new FileNameExtensionFilter(".jpg File", ".jpg"));
                    saveFile.addChoosableFileFilter(new FileNameExtensionFilter(".png File", ".png"));
                    saveFile.addChoosableFileFilter(new FileNameExtensionFilter(".gif File", ".gif"));
                    saveFile.addChoosableFileFilter(new FileNameExtensionFilter(".bmp File", ".bmp"));
                    String extension = saveFile.getFileFilter().getDescription().substring(0, 4);
                    if (saveFile.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            ImageIO.write(transformedImage, extension.substring(1), new File(saveFile.getSelectedFile().getAbsolutePath() + extension));
                            JOptionPane.showMessageDialog(new JFrame(), "The Image was saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            Logger.getLogger(PhotoEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case "Exit":
                    //Prompts user to see if they want to exit
                    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                    break;
                case "Restore to Original":
                    //Refreshes JPanel with original opened image
                    BufferedImage tempImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
                    Graphics2D graphic = tempImage.createGraphics();
                    graphic.drawImage(image, 0, 0, null);
                    graphic.dispose();
                    transformedImage = tempImage;
                    refreshPanel(image);
                    break;
                case "Vertical Flip":
                    //Flips image along x-axis
                    for (int i = 0; i < transformedImage.getWidth(); i++) {
                        for (int j = 0; j < transformedImage.getHeight() / 2; j++) {
                            int temporaryPixel = transformedImage.getRGB(i, j);
                            transformedImage.setRGB(i, j, transformedImage.getRGB(i, transformedImage.getHeight() - j - 1));
                            transformedImage.setRGB(i, transformedImage.getHeight() - j - 1, temporaryPixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Horizontal Flip":
                    //Flips image along y-axis
                    for (int i = 0; i < transformedImage.getWidth() / 2; i++) {
                        for (int j = 0; j < transformedImage.getHeight(); j++) {
                            int temporaryPixel = transformedImage.getRGB(i, j);
                            transformedImage.setRGB(i, j, transformedImage.getRGB(transformedImage.getWidth() - 1 - i, j));
                            transformedImage.setRGB(transformedImage.getWidth() - 1 - i, j, temporaryPixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Gray Scale":
                    //Creates GreyScale version of image
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            Color original = new Color(transformedImage.getRGB(j, i));
                            int red = (int) (original.getRed() * 0.299);
                            int green = (int) (original.getGreen() * 0.587);
                            int blue = (int) (original.getBlue() * 0.114);
                            Color grey = new Color(red + green + blue, red + green + blue, red + green + blue);
                            transformedImage.setRGB(j, i, grey.getRGB());
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Sepia Tone":
                    //Creates SepiaTone version of image
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            int pixel = transformedImage.getRGB(j, i);
                            int outputRed = (int) (0.393 * ((pixel >> 16) & 0xff) + 0.769 * ((pixel >> 8) & 0xff) + 0.189 * (pixel & 0xff));
                            int outputGreen = (int) (0.349 * ((pixel >> 16) & 0xff) + 0.686 * ((pixel >> 8) & 0xff) + 0.168 * (pixel & 0xff));
                            int outputBlue = (int) (0.272 * ((pixel >> 16) & 0xff) + 0.534 * ((pixel >> 8) & 0xff) + 0.131 * (pixel & 0xff));
                            int red = outputRed > 255 ? 255 : outputRed;
                            int green = outputGreen > 255 ? 255 : outputGreen;
                            int blue = outputBlue > 255 ? 255 : outputBlue;
                            pixel = (((pixel >> 24) & 0xff) << 24) | (red << 16) | (green << 8) | blue;
                            transformedImage.setRGB(j, i, pixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Invert Colour":
                    //Creates Inverted version of image
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            int pixel = transformedImage.getRGB(j, i);
                            int red = 255 - (pixel >> 16) & 0xff;
                            int green = 255 - (pixel >> 8) & 0xff;
                            int blue = 255 - pixel & 0xff;
                            pixel = ((pixel >> 24) & 0xff << 24) | (red << 16) | (green << 8) | blue;
                            transformedImage.setRGB(j, i, pixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Rotate Orientation":
                    //Rotates original image back and forth from portrait to landscape
                    BufferedImage newImage = new BufferedImage(transformedImage.getHeight(), transformedImage.getWidth(), transformedImage.getType());
                    for (int i = 0; i < transformedImage.getWidth(); i++) {
                        for (int j = 0; j < transformedImage.getHeight(); j++) {
                            newImage.setRGB(transformedImage.getHeight() - 1 - j, transformedImage.getWidth() - 1 - i, transformedImage.getRGB(i, j));
                        }
                    }
                    transformedImage = newImage;
                    refreshPanel(transformedImage);
                    break;
                case "Red Filter":
                    //Creates image with red filter
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            int pixel = transformedImage.getRGB(j, i);
                            pixel = (((pixel >> 24) & 0xff) << 24) | (((pixel >> 16) & 0xff) << 16);
                            transformedImage.setRGB(j, i, pixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Green Filter":
                    //Creates image with green filter
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            int pixel = transformedImage.getRGB(j, i);
                            pixel = (((pixel >> 24) & 0xff) << 24) | (((pixel >> 8) & 0xff) << 8);
                            transformedImage.setRGB(j, i, pixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Blue Filter":
                    //Creates image with blue filter
                    for (int i = 0; i < transformedImage.getHeight(); i++) {
                        for (int j = 0; j < transformedImage.getWidth(); j++) {
                            int pixel = transformedImage.getRGB(j, i);
                            pixel = (((pixel >> 24) & 0xff) << 24) | (pixel & 0xff);
                            transformedImage.setRGB(j, i, pixel);
                        }
                    }
                    refreshPanel(transformedImage);
                    break;
                case "Gaussian Blur":
                    //Creates an image with a gaussian blur filter
                    int radiuskernel = 4;
                    Kernel kernelUsed = new Kernel((radiuskernel * 2 + 1), (radiuskernel * 2 + 1), gaussianCalculator(radiuskernel));
                    ConvolveOp blurImage = new ConvolveOp(kernelUsed);
                    
                    transformedImage = blurImage.filter(transformedImage, null);
                    refreshPanel(transformedImage);
                    break;

                case "Bulge Effect":
                    //Creates image with bulge effect from the center
                    BufferedImage newImageOne = new BufferedImage(transformedImage.getWidth(), transformedImage.getHeight(), transformedImage.getType());
                    
                    for (int x = 0; x < transformedImage.getWidth(); x++) {
                        for (int y = 0; y < transformedImage.getHeight(); y++) {
                            int xRadius = transformedImage.getWidth() / 2;
                            int yRadius = transformedImage.getHeight() / 2;
                            double radius = Math.sqrt(Math.pow((x - xRadius), 2) + Math.pow((y - yRadius), 2));
                            radius = Math.pow(radius, 2) / transformedImage.getWidth();
                            double angle = Math.atan2((y - yRadius), (x - xRadius));
                            int newX = (int) (radius * Math.cos(angle)) + xRadius;
                            int newY = (int) (radius * Math.sin(angle)) + yRadius;
                            if (newX >= 0 && newX < transformedImage.getWidth() && newY >= 0 && newY < transformedImage.getHeight()) {
                                newImageOne.setRGB(x, y, transformedImage.getRGB(newX, newY));
                            }
                        }
                    }
                    transformedImage = newImageOne;
                    refreshPanel(newImageOne);
                    break;
            }
        }
    }

    public static float[] gaussianCalculator(int radius) {
        //Calculates a kernel using the gaussian blur function
        double[][] kernel = new double[radius * 2 + 2][radius * 2 + 2];
        float[] values = new float[(radius * 2 + 2) * (radius * 2 + 2)];
        int tracker = 0;
        double distance;
        int sigma = radius;
        int weight = 10;
        double sum = 0;
        double function = 1.0 / (2.0 * Math.PI * Math.pow(weight, 2));

        for (int i = -1 * sigma; i <= sigma; i++) {
            for (int j = -1 * sigma; j <= sigma; j++) {
                distance = ((Math.pow(i, 2) + (Math.pow(j, 2))) / (2 * Math.pow(weight, 2)));
                kernel[i + radius][j + radius] = function * Math.exp(-1 * distance);
                sum += (function * Math.exp(-1 * distance));
            }
        }
        for (int x = 0; x < radius * 2 + 1; x++) {
            for (int y = 0; y < radius * 2 + 1; y++) {
                kernel[x][y] = kernel[x][y] * (1 / sum);
                values[tracker++] = (float) kernel[x][y];
            }
        }
        return values;
    }

    public static void refreshPanel(BufferedImage image) {
        //Refreshes user panel with desired image
        panel.removeAll();
        panel.add(new JLabel(new ImageIcon(image)));
        frame.revalidate();
        frame.pack();
    }
}
