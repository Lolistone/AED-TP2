package aed;

class maxHeap<T extends Comparable<T>> {

	private T[] heap;
	private int capacidad;
	private int tamaño;
	
	private void swap(T[] array, int a, int b) {
		T elem = array[a];
		array[a] = array[b];
		array[b] = elem;
	}
	
	private int padre(int i) {
		return (i - 1) / 2;
	}

	private int izq(int i) {
		return 2 * i + 1;
	}
	
	private int der(int i) {
		return 2 * i + 2;
	}
	
	public maxHeap(int n) {
		capacidad = n;
		heap = (T[]) new Comparable[capacidad];
		tamaño = 0;
	}

	public void apilar(T elem) {
		if (tamaño == capacidad) {
            T[] nuevoHeap = (T[]) new Comparable[tamaño*2];
            for (int i = 0; i < tamaño; i++) {
                nuevoHeap[i] = heap[i];
            }
            heap = nuevoHeap;
		}

		int i = tamaño;
		heap[i] = elem;
		tamaño++;
	
		while (i != 0 && heap[i].compareTo(heap[padre(i)]) > 0) {
			swap(heap, i, padre(i));
			i = padre(i);
		}
	}
	

	public T max() {
		return heap[0];
	}
	

    // Devuelvo el maximo y lo elimino.
	public T desapilar() {

		if (tamaño == 1) {
			tamaño--;
			return heap[0];
		}

		T max = heap[0];
		heap[0] = heap[tamaño - 1];
		tamaño--;
		maxHeapify(0); // Restablezco el invariante.

		return max;
	}

	private void maxHeapify(int i) {
		int izq = izq(i);
		int der = der(i);

		int largest = i;
		if (izq < tamaño && heap[izq].compareTo(heap[largest]) > 0) {
			largest = izq;
		}
		if (der < tamaño && heap[der].compareTo(heap[largest]) > 0) {
			largest = der;
		}

		if (largest != i) {
			swap(heap, i, largest);
			maxHeapify(largest);
		}
	}

}


class maxHeapTest {
	public static void main(String[] args) {
		maxHeap<Tupla<Integer,Integer>> h = new maxHeap(11);
        Tupla<Integer,Integer> t = new Tupla<Integer,Integer>(1, 2);
        Tupla<Integer,Integer> t1 = new Tupla<Integer,Integer>(1, 25);
        Tupla<Integer,Integer> t2 = new Tupla<Integer,Integer>(1, 5);
        Tupla<Integer,Integer> t3 = new Tupla<Integer,Integer>(1, 1);
        Tupla<Integer,Integer> t4 = new Tupla<Integer,Integer>(1, 9);

		h.apilar(t);
		h.apilar(t1);
		h.apilar(t2);
		h.apilar(t3);
		h.apilar(t4);
        h.apilar(new Tupla<Integer,Integer>(1, 400));
		System.out.print(h.desapilar().toString() + " ");
		System.out.print(h.desapilar().toString() + " ");
		System.out.print(h.desapilar().toString() + " ");
		System.out.print(h.desapilar().toString() + " ");
		System.out.print(h.desapilar().toString() + " ");
		System.out.print(h.desapilar().toString() + " ");
	}
}

