
import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author Yunfan Yang
 */
public class DriverBonus {

    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);

        System.out.print("Name of class containing inspect method: ");
        String inspectMethodClassName = scan.nextLine();

        System.out.print("Name of class to inspect: ");
        String inspectClassName = scan.nextLine();

        System.out.print("Is recursive (y/n)? ");
        boolean recursive = scan.nextLine().equalsIgnoreCase("y");

        scan.close();

        try {
            Class<?> inspectMethodClass = Class.forName(inspectMethodClassName);
            Constructor<?> inspectMethodConstructor = inspectMethodClass.getConstructor();
            Object inspectMethodObject = inspectMethodConstructor.newInstance();
            Method inspectMethod = inspectMethodClass.getMethod("inspect", Object.class, boolean.class);

            Class<?> inspectClass = Class.forName(inspectClassName);
            Constructor<?> inspectConstructor = inspectClass.getConstructor();
            Object inspectObject = inspectConstructor.newInstance();

            inspectMethod.invoke(inspectMethodObject, inspectObject, recursive);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
}
