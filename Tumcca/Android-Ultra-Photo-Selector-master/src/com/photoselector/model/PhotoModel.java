package com.photoselector.model;

import com.google.gson.Gson;
import com.photoselector.ui.PhotoItem;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

/**
 * 
 * @author Aizaz
 *
 */


public class PhotoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String originalPath;
	private boolean isChecked;

	public transient PhotoItem photoView = null;

	private int index = -1;

//	private void writeObject(java.io.ObjectOutputStream out)
//	throws IOException {
//		    // write 'this' to 'out'..
//			out.writeChars(originalPath);
//			out.writeBoolean(isChecked);
//			out.writeInt(index);
//		   }
//	private void readObject(java.io.ObjectInputStream in)
//	throws IOException, ClassNotFoundException {
//		     // populate the fields of 'this' from the data in 'in'...
//			originalPath = in.
//		   }

	public PhotoModel(String originalPath, boolean isChecked) {
		super();
		this.originalPath = originalPath;
		this.isChecked = isChecked;
	}

	public PhotoModel(String originalPath) {
		this.originalPath = originalPath;
	}

	public PhotoModel() {
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public boolean isChecked() {
		return isChecked;
	}

//	@Override
//	public boolean equals(Object o) {
//		if (o.getClass() == getClass()) {
//			PhotoModel model = (PhotoModel) o;
//			if (this.getOriginalPath().equals(model.getOriginalPath())) {
//				return true;
//			}
//		}
//		return false;
//	}

	public void setChecked(boolean isChecked) {
		System.out.println("checked " + isChecked + " for " + originalPath);
		this.isChecked = isChecked;
		//photoView.setSelected(false);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PhotoModel)) {
			return false;
		}
		PhotoModel other = (PhotoModel) obj;
		if (originalPath == null) {
			if (other.originalPath != null) {
				return false;
			}
		} else if (!originalPath.equals(other.originalPath)) {
			return false;
		}
		return true;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		if(photoView!=null){
			photoView.setIndex(index);
		}
	}

	public String toJSON(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public void fromJSON(String string){
		Gson gson = new Gson();
		PhotoModel model = gson.fromJson(string, PhotoModel.class);
		this.originalPath = model.originalPath;
		this.isChecked = model.isChecked;
		this.index = model.index;
	}

}
