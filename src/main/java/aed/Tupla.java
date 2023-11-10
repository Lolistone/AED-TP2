package aed;

// Implementacion de Tupla donde la comparacion depende de la segunda componente.
public class Tupla<K, V extends Comparable<V>> implements Comparable<Tupla<K, V>> {

    private K key;
    private V value;
    
    // InvRep: true

    public Tupla(K k, V v){
        key = k;
        value = v;
    }

    public K getKey(){
        return key;
    }

    public V getVal(){
        return value;
    }

    public void modValue(V newV){
        value = newV;
    }
    
    @Override
    public int compareTo(Tupla<K,V> t) {
        if ((t.getVal()).compareTo(this.value) < 0) {
            return 1;
        }
        else if ((t.getVal()).compareTo(this.value) > 0) {
            return -1;
        }
        else {
            return 0;
        }
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("(" + this.getKey() + "," + this.getVal() + ")");
        return sb.toString();
    }
    
}
