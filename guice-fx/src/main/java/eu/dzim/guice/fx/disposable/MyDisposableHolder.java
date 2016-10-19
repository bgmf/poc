package eu.dzim.guice.fx.disposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDisposableHolder {
	
	private final List<Disposable> disposables = Collections.synchronizedList(new ArrayList<>());
	
	public MyDisposableHolder() {}
	
	public List<Disposable> getDisposables() {
		return disposables;
	}
}
