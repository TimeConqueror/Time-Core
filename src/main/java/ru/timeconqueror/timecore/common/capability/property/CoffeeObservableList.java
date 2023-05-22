package ru.timeconqueror.timecore.common.capability.property;

import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

import java.util.*;
import java.util.function.Supplier;

public class CoffeeObservableList<T> implements List<T>, IChangable {
    private boolean changed;
    private final List<T> list;

    private CoffeeObservableList(List<T> list) {
        this.list = list;
    }

    public static <T> CoffeeObservableList<T> observe(List<T> list) {
        if(list instanceof CoffeeObservableList<?>) {
            return (CoffeeObservableList<T>) list;
        }

        return new CoffeeObservableList<>(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Itr(list.iterator());
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        changed = true;

        return list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = list.remove(o);
        if(removed) changed = true;

        return removed;
    }

    @SuppressWarnings("SlowListContainsAll")
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean addedAny = list.addAll(c);
        if(addedAny) changed = true;
        return addedAny;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        boolean addedAny = list.addAll(c);
        if(addedAny) changed = true;
        return addedAny;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean removedAny = list.removeAll(c);
        if(removedAny) changed = true;
        return removedAny;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = list.retainAll(c);
        if(changed) this.changed = true;
        return changed;
    }

    @Override
    public void clear() {
        if(!isEmpty()) changed = true;

        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        changed = true;
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        changed = true;
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        changed = true;
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return new ListItr(list.listIterator());
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListItr(list.listIterator(index));
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean getChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean b) {
        this.changed = b;
    }

    private class Itr implements Iterator<T> {
        protected final Iterator<T> internalIterator;

        private Itr(Iterator<T> internalIterator) {
            this.internalIterator = internalIterator;
        }

        @Override
        public boolean hasNext() {
            return internalIterator.hasNext();
        }

        @Override
        public T next() {
            return internalIterator.next();
        }

        @Override
        public void remove() {
            changed = true;
            internalIterator.remove();
        }
    }

    private class ListItr extends Itr implements ListIterator<T> {

        private ListItr(ListIterator<T> internalIterator) {
            super(internalIterator);
        }

        private ListIterator<T> internalIterator() {
            return ((ListIterator<T>) internalIterator);
        }

        @Override
        public boolean hasPrevious() {
            return internalIterator().hasPrevious();
        }

        @Override
        public T previous() {
            return internalIterator().previous();
        }

        @Override
        public int nextIndex() {
            return internalIterator().nextIndex();
        }

        @Override
        public int previousIndex() {
            return internalIterator().previousIndex();
        }

        @Override
        public void set(T t) {
            changed = true;
            internalIterator().set(t);
        }

        @Override
        public void add(T t) {
            changed = true;
            internalIterator().add(t);
        }
    }
}
