package aed;
public class SistemaCNE {
    private String[] _nombresDistritos;
    private int[] _diputadosPorDistrito;
    private String[] _nombresPartidos;
    private int[] _rangoMesas;
    private maxHeap<Tupla<Integer, Integer>>[] _cocientesPorDistritos;
    private int[][] _diputadosPorPartido;
    private int[] _votosPresidenciales;
    private int[][] _votosDiputados;
    private BSBV _distritosComputado;
    private BSBV _mesasRegistradas;
    private int _primero;
    private int _segundo;
    private int _votosTotales;

    // InvRep : 

    public class VotosPartido{
        private int presidente;
        private int diputados;
        VotosPartido(int presidente, int diputados){this.presidente = presidente; this.diputados = diputados;}
        public int votosPresidente(){return presidente;}
        public int votosDiputados(){return diputados;}
    }

    public SistemaCNE(String[] nombresDistritos, int[] diputadosPorDistrito, String[] nombresPartidos, int[] ultimasMesasDistritos) {

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
        _distritosComputado = new BSBV(nombresDistritos.length);
        _mesasRegistradas = new BSBV(ultimasMesasDistritos[ultimasMesasDistritos.length - 1]);
        _cocientesPorDistritos = new maxHeap[nombresDistritos.length];

        _primero = -1;
        _segundo = -1;
        _votosTotales = 0;

        for(int i = 0; i < diputadosPorDistrito.length; i++){
            for(int j= 0; j < nombresPartidos.length; j++){
                _diputadosPorPartido[i][j] = 0;
                _votosDiputados[i][j] = 0;
                _votosPresidenciales[j] = 0;
            }
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

    public void registrarMesa(int idMesa, VotosPartido[] actaMesa) {

        int idDistrito = buscarID(idMesa);

        if (!(_mesasRegistradas.pertence(idMesa))){

            Tupla<Integer, Integer>[] votosPartido = new Tupla[_nombresPartidos.length - 1];
            int i = 0;
            for(int j = 0; j < _votosDiputados[0].length; j++){
                _votosDiputados[idDistrito][j] += actaMesa[j].votosDiputados();
                _votosPresidenciales[j] += actaMesa[j].votosPresidente();
                _votosTotales += actaMesa[j].votosPresidente();
                
                //Guardo en el heap solo a quienes superaron el umbral.
                if (j != _votosDiputados[0].length - 1) {
                    if (_votosTotales != 0 && votosDiputados(j,idDistrito)*100/(_votosTotales) > 3) {
                        votosPartido[i] = new Tupla<Integer,Integer>(j, votosDiputados(j, idDistrito));
                        i++;
                    }
                }
            }

            _cocientesPorDistritos[idDistrito] = new maxHeap(votosPartido);
            _distritosComputado.eliminar(idDistrito);
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

    public int[] resultadosDiputados(int idDistrito){
        if (!_distritosComputado.pertence(idDistrito) && _cocientesPorDistritos[idDistrito].max() != null){

            for (int i = 0; i < _diputadosPorDistrito[idDistrito]; i++) {
                Tupla<Integer, Integer> max = _cocientesPorDistritos[idDistrito].max();
                _diputadosPorPartido[idDistrito][max.getKey()]++; // Sumo un escaño al partido;

                int votos = votosDiputados(max.getKey(), idDistrito);
                int escaños = _diputadosPorPartido[idDistrito][max.getKey()];
                max.modValue(votos/(escaños+1)); // Calculo el cociente segun dHont; 

                _cocientesPorDistritos[idDistrito].modificarMaximo(max); // modifico el maximo y reestablezco el heap. 
                _distritosComputado.agregar(idDistrito); // Va a ser True mientras no se registre una nueva mesa.
            }
        }

        return _diputadosPorPartido[idDistrito];  
    }

    public boolean hayBallotage(){
        boolean res = true;
        double pjePrimero;
        double pjeSegundo;

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


