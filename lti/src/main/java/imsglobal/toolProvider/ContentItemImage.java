package imsglobal.toolProvider;

import java.net.URL;

public class ContentItemImage {
	/**
	 * Class to represent a content-item image object
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.2
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */
	
	private URL url;
	private int height;
	private int width;


	/**
	 * Class constructor.
	 *
	 * @param string $id      URL of image
	 * @param int    $height  Height of image in pixels (optional)
	 * @param int    $width   Width of image in pixels (optional)
	 */
    ContentItemImage(URL url, int height, int width)
    {

        setUrl(url);
        setHeight(height);
        setWidth(width);

    }
    
    ContentItemImage(URL url) {
    	setUrl(url);
    }

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
    
    

}
