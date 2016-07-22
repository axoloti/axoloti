package axoloti.utils;

import java.awt.Color;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class ColorConverter implements Converter<Color> {
    @Override
    public Color read(InputNode node) throws Exception {
        Integer red = Integer.parseInt(node.getAttributes().get("red").getValue());
        Integer green = Integer.parseInt(node.getAttributes().get("green").getValue());
        Integer blue = Integer.parseInt(node.getAttributes().get("blue").getValue());
        Integer alpha = Integer.parseInt(node.getAttributes().get("alpha").getValue());
        return new Color(red, green, blue, alpha);
    }

    @Override
    public void write(OutputNode node, Color color) {
        Integer red = color.getRed();
        Integer green = color.getGreen();
        Integer blue = color.getBlue();
        Integer alpha = color.getAlpha();

        node.setAttribute("red", red.toString());
        node.setAttribute("green", green.toString());
        node.setAttribute("blue", blue.toString());
        node.setAttribute("alpha", alpha.toString());
    }
}
