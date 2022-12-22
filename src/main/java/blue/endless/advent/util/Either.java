package blue.endless.advent.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Either<L, R> {
	private final L left;
	private final R right;
	
	private Either(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeft() { return left!=null; }
	public boolean isRight() { return right!=null; }
	
	public L left() { return left; }
	public R right() { return right; }
	
	public void ifLeft(Consumer<L> consumer) {
		if (left!=null) consumer.accept(left);
	}
	
	public void ifRight(Consumer<R> consumer) {
		if (right!=null) consumer.accept(right);
	}
	
	public boolean contains(Object value) {
		return
				(left!=null && left.equals(value)) ||
				(right!=null && right.equals(value));
	}
	
	public <T> T fold(Function<L, T> leftMap, Function<R,T> rightMap) {
		if (left!=null) return leftMap.apply(left);
		if (right!=null) return rightMap.apply(right);
		throw new IllegalStateException("This Either is neither!");
	}
	
	public Either<R, L> swap() {
		return new Either<>(right, left);
	}
	
	@Override
	public String toString() {
		if (left!=null) return left.toString();
		if (right!=null) return right.toString();
		return "neither";
	}
	
	public static <L, R> Either<L,R> left(L l) {
		return new Either<>(l, null);
	}
	
	public static <L, R> Either<L,R> right(R r) {
		return new Either<>(null, r);
	}
}