package tad.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tad.DAO.ICartDAO;
import tad.DAO.IOrderDAO;
import tad.DAO.IProductDAO;
import tad.entity.Account;
import tad.entity.Cart;
import tad.entity.OrderDetail;
import tad.entity.OrderDetailId;
import tad.entity.Orders;
import tad.entity.Product;
import tad.entity.Size;
import tad.entity.Variation;
import tad.utility.DefineAttribute;

@Controller
@RequestMapping(value = "/order")
public class UserOrderController {

	@Autowired
	private IOrderDAO orderDAO;
	@Autowired
	private ICartDAO cartDAO;
	@Autowired
	private IProductDAO productDAO;

	@RequestMapping(value = "orderDetail")
	public String detail(ModelMap model, HttpSession session, @RequestParam("orderId") int orderId) {
		Orders order = orderDAO.findOrder(orderId);
		model.addAttribute("order", order);
		return "order/orderDetail";

	}

	@RequestMapping("success")
	public String success(HttpSession session, @RequestParam(value = "totalPrice", defaultValue = "0") float totalPice,
			ModelMap model) {
		Account account = (Account) session.getAttribute(DefineAttribute.UserAttribute);
		if (account == null) {
			return "redirect:/guest.htm";
		}
		List<Cart> listCarts = cartDAO.getCart(account.getAccountId());
		Orders orders = new Orders();
		
		Calendar c1 = Calendar.getInstance();
		Date in = new Date();
		LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
		Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		orders.setAccount(account);
		orders.setOrderTime(out);

		c1.setTime(out);
		c1.add(Calendar.MINUTE, 30);

		orders.setDeliveryTime(c1.getTime());
		orders.setStatus(0);
		orders.setPrice(totalPice);
		model.addAttribute("orders", orders);
		if (account.getDefaultAddress() != null)
			orders.setDefaultAddress(account.getDefaultAddress().getFullAddress());

		orderDAO.insertOrder(orders);

		for (Cart c : listCarts) {
			
			
			
			List<Size> listsize=productDAO.getSizeProductsbySizeName(c.getSize());
			Size sizeid=listsize.get(0);
			Variation VarquantityShop=productDAO.QuantityBySizeId(sizeid.getSizeId(), c.getProduct().getProductId());
			int quantityShop=VarquantityShop.getQuantity();
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setId(new OrderDetailId(orders.getOrderId(), c.getProduct().getProductId()));
			orderDetail.setProduct(c.getProduct());
			orderDetail.setOrder(orders);
			orderDetail.setQuantity(c.getQuantity());
			orderDetail.setSize(sizeid);
			orderDetail.setPrice(Double.valueOf(String.valueOf(c.getProduct().getPrice()*c.getQuantity()) ));
			if (c.getProduct().getCoupon() != null && c.getProduct().getCoupon().checkVaildCoupon())
				orderDetail.setCoupon(c.getProduct().getCoupon().getDiscount());
			
			// aaaaaaaaaaaaaaaaaaaaaaaaaaaa
			
			Product product1 = VarquantityShop.getProduct();
		    product1.setQuantity(product1.getQuantity() - c.getQuantity()); 
		    productDAO.updateProduct(product1);

			// Xoa so luong ton sp
			Product product =VarquantityShop.getProduct();
			product.setQuantity(quantityShop - c.getQuantity());
			Variation vari= new Variation();
			vari.setVariationId(VarquantityShop.getVariationId());
			vari.setQuantity(quantityShop - c.getQuantity());
			vari.setProduct(product);
			vari.setSize(sizeid);
		//	productDAO.updateProduct(product);
			productDAO.updateVariation(vari);
			orderDAO.insertOrderDetail(orderDetail);
		}
		int s = cartDAO.removeCart(account.getAccountId());
		session.setAttribute("totalCart", 0);

		return "order/success";
	}

	@RequestMapping(value = "cancelRequest")
	public String cancleRequest(HttpSession session, HttpServletRequest request,
			@RequestParam(value = "orderId") int orderId) {
		Orders orders = orderDAO.findOrder(orderId);
		// Cap nhat lai so luong ton sp
		for (OrderDetail d : orders.getOrderDetails()) {
			Product product = d.getProduct();
			product.setQuantity(product.getQuantity() + d.getQuantity());
			productDAO.updateProduct(product);
			
			Variation variation = productDAO.QuantityBySizeId(d.getSize().getSizeId(), d.getProduct().getProductId());
	        if (variation != null) {
	            variation.setQuantity(variation.getQuantity() + d.getQuantity());
	            variation.setProduct(product);
	            variation.setSize(d.getSize());
	            productDAO.updateVariation(variation);
	        }
		}
		orders.setStatus(3);
		orderDAO.update(orders);
		
		return "redirect:" + request.getHeader("Referer");

	}

}
