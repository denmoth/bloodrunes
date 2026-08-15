import java.io.PrintWriter;
public class Reflect3 {
    public static void main(String[] args) throws Exception {
        PrintWriter writer = new PrintWriter("scratch/guigraphics.txt", "UTF-8");
        try {
            Class<?> clazz = Class.forName("net.minecraft.client.gui.GuiGraphics");
            writer.println("GuiGraphics found in net.minecraft.client.gui");
        } catch (Exception e) {
            writer.println("GuiGraphics not found in net.minecraft.client.gui");
        }
        writer.close();
    }
}
