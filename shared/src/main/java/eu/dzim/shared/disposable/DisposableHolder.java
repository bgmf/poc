package eu.dzim.shared.disposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisposableHolder {
	
	private final List<Disposable> disposables = Collections.synchronizedList(new ArrayList<>());
	
	public DisposableHolder() {}
	
	public List<Disposable> getDisposables() {
		return disposables;
	}
	
	public void disposeAll() {
		Collections.synchronizedList(new ArrayList<>(disposables)).stream().forEach(disposable -> {
			if (disposable != null)
				disposable.dispose();
		});
	}
}
