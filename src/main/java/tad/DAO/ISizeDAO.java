package tad.DAO;

import java.util.List;

import tad.entity.Size;
import tad.entity.Variation;

public interface ISizeDAO {
	public Size getSizeBySizeID(int sizeId);
	
	public boolean insertVariation(Variation variation);
	Size getSize(int id);


    List<Size> getAllSizes();
}
