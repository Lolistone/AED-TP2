package aed;

public class BSBV {
    private boolean[] arr;
    private int tamaño;
    
    public BSBV(int n) {
        tamaño = 0;
        arr = new boolean[n];
        for (int i = 0; i < n; i++){
            arr[i] = false;
        }
    }
    
    public boolean pertence(int i) {
        return arr[i];
    }
    
    public void eliminar(int i) {
        arr[i] = false;
        tamaño--;
    }

    public void agregar(int i) {
        arr[i] = true;
        tamaño++;
    }

    public int longitud(){
        return tamaño;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append("{");
        for (int i = 0; i < arr.length; i ++) {
            if (arr[i]) {sb.append(i + ",");}
        }
        if (arr[arr.length-1]){sb.append(arr.length-1);}
        sb.append("}");
        return sb.toString();
    }

}
