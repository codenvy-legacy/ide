<%@page import="test.Product"%>
<html>
<body bgcolor="white">
   <div>
      <table border="1">
         <thead>
            <tr>
               <th>Product</th>
               <th>Description</th>
               <th>Price</th>
            </tr>
         </thead>
         <tbody>
            <%
               {
            %>
            <%
               java.util.Collection<Product> products = (java.util.Collection<Product>)request.getAttribute("items");
            %>
            <%
               for (Product p : products)
                  {
            %>
            <tr>
               <td><%=p.getName()%></td>
               <td><%=p.getDescription()%></td>
               <td><%=p.getPrice()%></td>
            </tr>
            <%
               }
            %>
            <%
               }
            %>
         </tbody>
      </table>
   </div>
   <div style="font-size: 150%; color: #850F0F">
      <form method="post" action="cart">
         <br>Please select item to add or remove: <br> add item: <select name="item">
            <%
               {
            %>
            <%
               java.util.Collection<Product> products = (java.util.Collection<Product>)request.getAttribute("products");
            %>
            <%
               for (Product p : products)
                  {
            %>
            <option><%=p.getName()%></option>
            <%
               }
            %>
            <%
               }
            %>
         </select> <br /> <br /> <input type=submit name="submit" value="add"> <input type=submit name="submit"
            value="remove">
      </form>
   </div>
</body>
</html>
