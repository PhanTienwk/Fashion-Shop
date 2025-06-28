package tad.Memento;

//chứa snapshot của name và image
public class CategoryMemento {
	private final int id;
	private final String name;
	private final String image;

	public CategoryMemento(int id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}

	//chỉ có getter để đảm bảo tính bất biến
	public int getId() { return id; }
	public String getName() { return name; }
	public String getImage() { return image; }
}
