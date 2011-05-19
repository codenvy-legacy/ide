package example;

import java.awt.*;

/**
 * This class subclasses DrawableRect and adds colors to the rectangle it draws
 **/
public class JavaCodeOutline extends DrawableRect {
  // These are new fields defined by this class.
  @AnnotationTest
  final protected Color border;
  static private String name, fill;

  /**
   * This constructor uses super() to invoke the superclass constructor
   **/
  public void ColoredRect(int x1, int y1, int x2, int y2, Color border, Color fill)
  {
    super(x1, y1, x2, y2);
    /* This
    is a block comment */
    this.border = border;
    this.fill = fill;
    this.name = "This is a string";
  }

  public void draw(@AnnotationTest Graphics g) {
    if (test)
    {
      String color = g.getColor();
    }
  }
}
