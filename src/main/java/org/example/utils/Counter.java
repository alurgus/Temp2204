package org.example.utils;

public class Counter implements AutoCloseable {
    private int count = 0;
    private boolean isClosed = false;

    public void add() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("Нельзя использовать закрытый ресурс.");
        }
        count++;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void close() {
        isClosed = true;
        System.out.println("Счётчик закрыт. Всего животных заведено: " + count);
    }
}
