package test;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CartController implements Controller
{
   private final Map<String, Product> products;
   private final Map<String, Product> items;

   public CartController()
   {
      products = new HashMap<String, Product>(3);
      products
         .put(
            "The MacBook Pro",
            new Product(
               "The MacBook Pro",
               "The new MacBook Pro. State-of-the-art processors. All-new graphics. Breakthrough high-speed I/O. Three very big leaps forward.",
               2499.99));
      products.put("The Mac Pro", new Product("The Mac Pro",
         "The new Mac Pro. With up to 12 cores of processing power, it's the fastest Mac ever.", 2999.99));
      products
         .put(
            "The MacBook Air",
            new Product(
               "The MacBook Air",
               "Every millimeter a Mac. Underneath all that thinness is a full-size, fully capable Mac that can do practically everything its larger siblings can do. Minus a pound or two.",
               999.99));
      items = new HashMap<String, Product>();
   }

   @Override
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
   {
      String submit = request.getParameter("submit");
      if (submit == null || "add".equals(submit))
      {
         String item = request.getParameter("item");
         Product p = products.get(item);
         if (p != null)
         {
            items.put(p.getName(), p);
         }
      }
      else if ("remove".equals(submit))
      {
         String item = request.getParameter("item");
         items.remove(item);
      }

      ModelAndView view = new ModelAndView("cart_view");
      view.addObject("products", products.values());
      view.addObject("items", items.values());
      return view;
   }
}
