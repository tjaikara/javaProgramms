package generics.fixedqueue;

public interface IGenQ <T> {

    void put(T ch) throws QueueFullException;

    T get() throws QueueEmptyException;
}
