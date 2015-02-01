package com.example.androparent;

import com.parse.ParseFile;

public class Child {
	public ParseFile photo;
	public String name;

	public Child() {
		super();
	}

	public Child(ParseFile photo, String name) {
		super();
		this.photo = photo;
		this.name = name;
	}
}
