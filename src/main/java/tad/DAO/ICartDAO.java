package tad.DAO;

import java.util.List;

import tad.entity.Cart;
import tad.entity.Coupon;

public interface ICartDAO {

	boolean insertCart(Cart cart);

	boolean deleteCart(Cart cart);

	boolean updateCart(Cart cart);
	Cart getCartsize(int accountId, int productId,int size);

	Coupon getCoupon(String coupon);

	int updateQuantity(int productId, int quantity);
	
	int updateQuantity1(int productId, int quantity,int size);

	Cart getCart(int accountId, int productId);

	List<Cart> getCart(int accountId);

	int removeCart(int accountId);

	int updateCoupon(String coupon, int amount);

}
