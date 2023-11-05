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

	public maxHeap(T[] array) {
		capacidad = array.length;
		tamaño = capacidad;
		heap = (T[]) new Comparable[capacidad];
		heap = array;
		for (int i = (tamaño-1)/2; i>=0 ; i--) {
			bajar(i);
		}
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
	
		subir(i); // Restablezco el invariante.
	}

	private void subir(int i) {
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
		bajar(0); // Restablezco el invariante.

		return max;
	}

	private void bajar(int i) {
		int largest = i;
		boolean prioridad = true;

		while (esHoja(largest) && prioridad) {

			i = largest;
			int izq = izq(largest);
			int der = der(largest);

			if (izq < tamaño && heap[izq].compareTo(heap[largest]) >= 0) {
				largest = izq;
			}

			if (der < tamaño && heap[der].compareTo(heap[largest]) >= 0) {
				largest = der;
			}

			else if (der >= tamaño || heap[izq].compareTo(heap[largest]) < 0){
				prioridad = false; // Si no hay un hijo mas grande el ciclo termina.
			}

			if (i != largest) {
				swap(heap, i, largest);
			}
		}
	}

	private boolean esHoja(int i) {
		boolean res = (izq(i) < tamaño);
		return res;
	}

public static void main(String[] args) {
		Integer[] arr;
		arr = new Integer[]{1,4,5,3, 57777, 2, 6, 99, 7, 43, 45, 23, 777, 100000, 11, 12, 13, 5424, 543, 27};
		maxHeap<Integer> mHeap = new maxHeap(arr); 
        System.out.println(
            "Array representation of Heap is:");

        for (int i = 0; i < arr.length; ++i)
            System.out.print(mHeap.desapilar() + " ");
 
        System.out.println();
}

}