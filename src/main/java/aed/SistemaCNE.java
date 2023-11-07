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
    private boolean[] _distritosComputado;
    private boolean[] _mesasRegistradas;
    private int _primero;
    private int _segundo;
    private int _votosTotales;

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
        _mesasRegistradas = new boolean[ultimasMesasDistritos[ultimasMesasDistritos.length - 1]];
        _diputadosPorPartido = new int[nombresDistritos.length][nombresPartidos.length];
        _votosPresidenciales = new int[nombresPartidos.length];
        _votosDiputados = new int[nombresDistritos.length][nombresPartidos.length];
        _distritosComputado = new boolean[nombresDistritos.length];
        _cocientesPorDistritos = new maxHeap[nombresDistritos.length];

        for(int i = 0; i < diputadosPorDistrito.length; i++){
            for(int j= 0; j < nombresPartidos.length; j++){
                _diputadosPorPartido[i][j] = 0;
                _votosDiputados[i][j] = 0;
                _votosPresidenciales[j] = 0;
            }
            _distritosComputado[i] = false;
        }
        /*
        for(int j= 0; j < nombresPartidos.length; j++){
            _votosPresidenciales[j] = 0;
        } */
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
                } else if (idMesa < _rangoMesas[mid - 1]){
                    fin = mid - 1;
                } else if (idMesa >= _rangoMesas[mid]){
                    inicio = mid + 1;
                }
            } else {
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
        if (!(_mesasRegistradas[idMesa])){
            Tupla<Integer, Integer>[] votosPartido = new Tupla[_nombresPartidos.length - 1];
            for(int j = 0; j < _votosDiputados[0].length; j++){
                _votosDiputados[idDistrito][j] += actaMesa[j].votosDiputados();
                _votosPresidenciales[j] += actaMesa[j].votosPresidente();
                _votosTotales += actaMesa[j].votosPresidente();
                if (j != _votosDiputados[0].length - 1) {
                    votosPartido[j] = new Tupla<Integer,Integer>(j, votosDiputados(j, idDistrito));
                }
            }
            // VOTOS POR PARTIDO DEBE TENER PARTIDO QUE SUPERE EL UMBRAL (> 3%)
            _cocientesPorDistritos[idDistrito] = new maxHeap(votosPartido); 
            _mesasRegistradas[idMesa] = true;
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
        if (!_distritosComputado[idDistrito]){
            for (int i = 0; i < _diputadosPorDistrito[idDistrito]; i++) {
                Tupla<Integer, Integer> max = _cocientesPorDistritos[idDistrito].desapilar();
                _diputadosPorPartido[idDistrito][max.getKey()] += 1;
                int votos = votosDiputados(max.getKey(), idDistrito);
                int escaños = _diputadosPorPartido[idDistrito][max.getKey()];
                max.modValue(votos/(escaños+1));
                _cocientesPorDistritos[idDistrito].apilar(max);
                _distritosComputado[idDistrito] = true;
            }
        }
        return _diputadosPorPartido[idDistrito];  
    }

    public boolean hayBallotage(){
        int porcentajePrimero = (_primero/_votosTotales) * 100;
        int porcentajeSegundo = (_segundo/_votosTotales) * 100;
        boolean res = true;

        if(porcentajePrimero > 45){
            res = false;
        } else if (porcentajePrimero > 40 && (porcentajePrimero - porcentajeSegundo) >= 10){
            res = false;
        }
        return res;
    }

    private void buscarMaximos(){
        if (_votosPresidenciales.length > 2){
            if (_votosPresidenciales[0] >= _votosPresidenciales[1]){
                _primero = _votosPresidenciales[0];
                _segundo = _votosPresidenciales[1];
            } else {
                _primero = _votosPresidenciales[1];
                _segundo = _votosPresidenciales[0];
            }
            if(_votosPresidenciales.length > 3)
                for(int i = 2; i < _votosPresidenciales.length - 1; i++){
                    if (_votosPresidenciales[i] > _primero){
                        _segundo = _primero;
                        _primero = _votosPresidenciales[i];
                    } else if (_votosPresidenciales[i] > _segundo){
                        _segundo = _votosPresidenciales[i];
                    }
            }
        } else if (_votosPresidenciales.length == 2){
            _primero = _votosPresidenciales[0];
            _segundo = _votosPresidenciales[0];
        } else {
            _primero = -1;
            _segundo = -1;
        }
    }
}


