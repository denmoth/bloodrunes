import java.lang.reflect.Method;
import java.io.PrintWriter;
public class Reflect {
    public static void main(String[] args) throws Exception {
        PrintWriter writer = new PrintWriter("scratch/methods.txt", "UTF-8");
        Class<?> clazz = Class.forName("net.minecraft.client.multiplayer.ClientPacketListener");
        for (Method m : clazz.getDeclaredMethods()) {
            writer.println("ClientPacketListener: " + m.getName() + " -> " + m.getReturnType().getSimpleName());
        }
        clazz = Class.forName("net.minecraft.client.multiplayer.ClientLevel");
        for (Method m : clazz.getDeclaredMethods()) {
            writer.println("ClientLevel: " + m.getName() + " -> " + m.getReturnType().getSimpleName());
        }
        writer.close();
    }
}
