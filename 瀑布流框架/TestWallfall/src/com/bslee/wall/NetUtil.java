package com.bslee.wall;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class NetUtil {
	public static List<Pic> getListPic(String url) {
		List<Pic> map = new ArrayList<Pic>();
		try {
			URL uri = new URL(url);
			InputStream is = uri.openStream();
			Source source = new Source(is);
			List<Element> data = source.getAllElements();
			for (int m = 0; m < data.size(); m++) {
				Element e = data.get(m);
				if (e.getName().equals("img")) {
					if (e.getAttributeValue("src2") != null) {
						map.add(new Pic(e.getAttributeValue("alt"), e
								.getAttributeValue("src2")));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
