package aed;
public class SistemaCNE {

    private String[] _nombresDistritos;
    private String[] _nombresPartidos;
    private int[] _diputadosPorDistrito;
    private int[] _rangoMesas;
    
    /*
    |_nombresDistritos| == |_diputadosPorDistrito| == |_diputadosPorPartido| == |_votosDiputados| == |_rangoMesas| y luego    
    Para todo i  si i esta entre 0 y |_rangoMesas|- 1 entonces luego 0 <= _rangoMesas[i] < _rangoMesas[i+1] y _rangoMesas[i]
    en _nombresPartidos y _nombresDistritos no hay repetidos.
    */
    
    private int[][] _diputadosPorPartido;
    
    /*
    para todo partido d, los elementos de _diputadosPorPartido[d] son positivos.
    */

    private int[][] _votosDiputados;

    /*
    |_nombresPartidos| == |_votosPresidenciales| == |_diputadosPorPartido[i]| == |_votosDiputados[i]| para todo i entre 0 y |_diputadosPorPartido|

    en _votosPresidenciales todos los elementos son mayores o iguales a cero. 
    */
    
    private maxHeap<Tupla<Integer, Double>>[] _cocientesPorDistritos;       
    private BSBV _distritosComputados;
    private BSBV _mesasRegistradas;
    
    /*
    |_distritosComputados| == |_nombresDistritos| y |_mesasRegistradas| == _rangoMesas[|_rangoMesas| - 1] 
    
    para todo distrito d y partido p
    (0 <= d < |_nombresDistritos| y 0 <= p < |_nombresPartidos| - 1) y luego p pertenece a _distritosComputados y luego _votosDiputados[d][p] / _votosTotales > 0.3) 
    sii (p, _votosDiputados[d][p] / _diputadosPorPartido[d][p] + 1) pertenece a _cocientesPorDistrito[d]
    */
    
    private int _primero;
    private int _segundo;
    
    /*
    |_votosPresidenciales| > 2 y hay mesas registradas y luego 0 <= _primero ,_segundo < |_nombresPartidos| y para todo partido p != de _primero y segundo tengo que
    _votosPresidenciales[_primero] >= _votosPresidenciales[_segundo] >= _votosPresidenciales[p]
    ó
	|_votosPresidenciales| == 2 y hay mesas registradas entonces luego _primero == 0 y _segundo == -1
    ó
    |_votosPresidenciales| <= 1 entonces luego _primero == _segundo == -1 
    ó
    no hay mesas registradas y _primero == _segundo == -1)
    */
    
    private int _votosTotales;
    private int[] _votosPresidenciales;
    
    //la suma de los elementos de _votosPresidenciales debe ser igual a _votosTotales

    /* 
    InvRep : 
    
    |_nombresDistritos| == |_diputadosPorDistrito| == |_diputadosPorPartido| == |_votosDiputados| == |_rangoMesas| yLuego
    
    |_nombresPartidos| == |_votosPresidenciales| == |_diputadosPorPartido[i]| == |_votosDiputados[i]| para todo i entre 0 y |_diputadosPorPartido| yLuego
    
    en _nombresPartidos y _nombresDistritos no hay repetidos y
    
    para todo partido d, los elementos de _diputadosPorPartido[d] son positivos y
    
    en _votosPresidenciales todos los elementos son mayores o iguales a cero y
    
    |_distritosComputados| == |_nombresDistritos| y |_mesasRegistradas| == _rangoMesas[|_rangoMesas| - 1] yLuego
  
    para todo distrito d y partido p
    (0 <= d < |_nombresDistritos| y 0 <= p < |_nombresPartidos| - 1) y luego p pertenece a _distritosComputados y luego _votosDiputados[d][p] / _votosTotales > 0.3) 
    sii (p, _votosDiputados[d][p] / _diputadosPorPartido[d][p] + 1) pertenece a _cocientesPorDistrito[d] y

    (|_votosPresidenciales| > 2 y hay mesas registradas y luego 0 <= _primero ,_segundo < |_nombresPartidos| y para todo partido p != de _primero y segundo tengo que
    _votosPresidenciales[_primero] >= _votosPresidenciales[_segundo] >= _votosPresidenciales[p]
    ó
	|_votosPresidenciales| == 2 y hay mesas registradas entonces luego _primero == 0 y _segundo == -1
    ó
    |_votosPresidenciales| <= 1 entonces luego _primero == _segundo == -1 
    ó
    no hay mesas registradas y _primero == _segundo == -1) y

    la suma de los elementos de _votosPresidenciales debe ser igual a _votosTotales
    
	*/
	
    public class VotosPartido{
        private int presidente;
        private int diputados;
        VotosPartido(int presidente, int diputados){this.presidente = presidente; this.diputados = diputados;}
        public int votosPresidente(){return presidente;}
        public int votosDiputados(){return diputados;}
    }
    
    // O(P + D + P*D) = O(P*D)
    public SistemaCNE(String[] nombresDistritos, int[] diputadosPorDistrito, String[] nombresPartidos, int[] ultimasMesasDistritos) {

        // Las siguientes son O(p) u O(D)
        _nombresDistritos = nombresDistritos;
        _diputadosPorDistrito = diputadosPorDistrito;
        _nombresPartidos = nombresPartidos;
        _rangoMesas = ultimasMesasDistritos;

        _diputadosPorPartido = new int[nombresDistritos.length][nombresPartidos.length];
        // Guardo #Votos de cada partido;
        _votosPresidenciales = new int[nombresPartidos.length];
        _votosDiputados = new int[nombresDistritos.length][nombresPartidos.length];
        // Guardo los distritos "computados". Es decir, cada vez que calculo los diputados
        // en un distrito este va a figurar como computado hasta que una nueva mesa se registre;
        _distritosComputados = new BSBV(nombresDistritos.length);
        _mesasRegistradas = new BSBV(ultimasMesasDistritos[ultimasMesasDistritos.length - 1]);
        _cocientesPorDistritos = new maxHeap[nombresDistritos.length];

        _primero = -1;
        _segundo = -1;
        _votosTotales = 0;

        // O(D * P)
        for(int i = 0; i < diputadosPorDistrito.length; i++){
            for(int j= 0; j < nombresPartidos.length; j++){
                _diputadosPorPartido[i][j] = 0;
                _votosDiputados[i][j] = 0;
            }
        }
        // O(p)
        for(int j= 0; j < nombresPartidos.length; j++){
            _votosPresidenciales[j] = 0;
        }        
    }

    public String nombrePartido(int idPartido) {
        return _nombresPartidos[idPartido];
    }

    public String nombreDistrito(int idDistrito) {
        return _nombresDistritos[idDistrito];
    }

    public int diputadosEnDisputa(int idDistrito) {
        return _diputadosPorDistrito[idDistrito];
    }

    public String distritoDeMesa(int idMesa) {
        int idDistrito = buscarID(idMesa);
        return nombreDistrito(idDistrito);
    }

    // Busqueda binaria es O(log p)
    private int buscarID(int idMesa){
        int inicio = 0;
        int fin = _rangoMesas.length - 1;
        int mid = 0;
        boolean encontrado = false;

        while (inicio <= fin && !(encontrado)){
            mid = inicio + (fin-inicio)/2;
            if (mid != 0){

                if (_rangoMesas[mid - 1] <= idMesa && idMesa < _rangoMesas[mid]){
                    encontrado = true;
                } 
                else if (idMesa < _rangoMesas[mid - 1]){
                    fin = mid - 1;
                } 
                else if (idMesa >= _rangoMesas[mid]){
                    inicio = mid + 1;
                }

            } 
            else {

                if (idMesa >= _rangoMesas[mid]){
                    mid++;
                }
                encontrado = true;

            }              
        }
        return mid;
    } 

    // O(log D + P) peor caso
    public void registrarMesa(int idMesa, VotosPartido[] actaMesa) {

        int idDistrito = buscarID(idMesa); // busqueda binaria O(log D)

        if (!(_mesasRegistradas.pertence(idMesa))){
            
            Tupla<Integer, Double>[] votosPartido = new Tupla[_nombresPartidos.length - 1]; // O(p)
            int i = 0;

            for(int j = 0; j < _votosDiputados[0].length; j++){

                // Las siguientes operaciones son O(1)
                _votosDiputados[idDistrito][j] += actaMesa[j].votosDiputados(); 
                _votosPresidenciales[j] += actaMesa[j].votosPresidente();
                _votosTotales += actaMesa[j].votosPresidente();
                
                //Guardo en el heap solo a quienes superaron el umbral.
                if (j != _votosDiputados[0].length - 1) {
                    if (_votosTotales != 0 && votosDiputados(j,idDistrito)*100/(_votosTotales) > 3) {
                        
                        // Crear una tupla es O(1) y asignar también
                        votosPartido[i] = new Tupla<Integer,Double>(j, (double)votosDiputados(j, idDistrito));
                        i++;
                    }
                }
            }

            _cocientesPorDistritos[idDistrito] = new maxHeap(votosPartido, i);
            _distritosComputados.eliminar(idDistrito);
            _mesasRegistradas.agregar(idDistrito);
            buscarMaximos();

        }
    }

    public int votosPresidenciales(int idPartido) {
        return _votosPresidenciales[idPartido];
    }

    public int votosDiputados(int idPartido, int idDistrito) {
        return _votosDiputados[idDistrito][idPartido];
    }

    //O(D_d * log p)
    public int[] resultadosDiputados(int idDistrito){

        // O(1) el pertenece del BSBV es cte y ver el maximo del heap también.
        if (!_distritosComputados.pertence(idDistrito) && _cocientesPorDistritos[idDistrito].max() != null){

            for (int i = 0; i < _diputadosPorDistrito[idDistrito]; i++) { //Itero D_d veces

                // O(1)
                Tupla<Integer, Double> max = _cocientesPorDistritos[idDistrito].max();
                _diputadosPorPartido[idDistrito][max.getKey()]++; // Sumo un escaño al partido;

                // O(1)
                int votos = votosDiputados(max.getKey(), idDistrito);
                int escaños = _diputadosPorPartido[idDistrito][max.getKey()];
                max.modValue((double)votos/(escaños+1));

                // O(log p) en el peor caso. Modifico el maximo y baja "toda una rama".
                _cocientesPorDistritos[idDistrito].modificarMaximo(max); // modifico el maximo y reestablezco el heap. 
                // O(1) asignación.
                _distritosComputados.agregar(idDistrito); // Va a ser True mientras no se registre una nueva mesa.
            }
        }

        return _diputadosPorPartido[idDistrito];  
    }


    public boolean hayBallotage(){
        boolean res = true;
        double pjePrimero;
        double pjeSegundo;

        // Unicamente hay comparaciones O(1);

        if (_primero != -1 && _segundo != -1) {
            pjePrimero = porcentaje(_primero);
            pjeSegundo = porcentaje(_segundo);
        }

        else if (_primero != -1 && _segundo == -1) {
            pjePrimero = porcentaje(_primero); 
            pjeSegundo = 0;            
        }
        // Si no hay partidos segun la especificacion hay ballotage
        else {
            return res;
        }


        if (pjePrimero >= 45){
            res = false;
        } 
        else if (pjePrimero > 40 && (pjePrimero - pjeSegundo) >= 10){
            res = false;
        }

        return res;
    }

    private double porcentaje(int idPartido) {
        double res = (double)_votosPresidenciales[idPartido]/_votosTotales;
        return res * 100;
    }

    // En el peor caso es de O(p) pues recorre todo el array;
    private void buscarMaximos(){
        if (_votosPresidenciales.length > 2){

            if (_votosPresidenciales[0] >= _votosPresidenciales[1]){
                _primero = 0;
                _segundo = 1;
            } 
            else {
                _primero = 1;
                _segundo = 0;
            }

            if (_votosPresidenciales.length > 3) {

                for(int i = 2; i < _votosPresidenciales.length - 1; i++){
                    if (_votosPresidenciales[i] > _votosPresidenciales[_primero]){
                        _segundo = _primero;
                        _primero = i;
                    } 
                    else if (_votosPresidenciales[i] > _votosPresidenciales[_segundo]){
                        _segundo = i;
                    }
                }

            }
        }

        else if (_votosPresidenciales.length == 2) {
            _primero = 0;
            _segundo = 0;
        } 

    }
}


