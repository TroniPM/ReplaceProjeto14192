package replaceprojeto;

/**
 *
 * @author pmdsilva
 * @date 02-mar-2021
 */
public class ReplaceProjeto {

    public static String PATTERN_EVENTS = ".events.";
    public static String PATTERN_LINKEDDATA_INLINE = ", this.getWLDFocused())";
    public static String PATTERN_LINKEDDATA_MULTIPLELINE = "}, this.getWLDFocused());";

    public static boolean SOBRESCREVER_ARQUIVO_ORIGINAL = true;
    public static boolean ADICIONAR_INJECT_ATEPACVARIABLES = true;
    public static boolean ADICIONAR_METODO_AUXILIAR = true;

    public static String[] FILES = new String[]{
        "C:\\GitHub\\tasy\\src\\app\\atePac\\commons\\vitalSignsGeneric\\VitalSignsGenericMonitoring.js",
        "C:\\GitHub\\tasy\\src\\app\\atePac\\commons\\vitalSignsAndMonitoring\\vitalSignsAndGeneralMonitoring\\AtendimentoSinalVital.js"
    };

    public static void main(String[] args) {
        IO.deletarArquivos();
        String[] arquivos = FILES;
        System.out.println("Arquivos configurados:");
        for (String names : arquivos) {
            System.out.println(names);
        }

        System.out.println("--------------------------------------");
        System.out.println("CONFIGURAÇÃO:");
        System.out.println(SOBRESCREVER_ARQUIVO_ORIGINAL ? "SUBSTITUIR ARQUIVOS ORIGINAIS" : "SALVAR NOVOS ARQUIVOS NA C:/");
        System.out.println((ADICIONAR_INJECT_ATEPACVARIABLES ? "" : "NÃO ") + "ADICIONAR INJECT atePacEHVariables AUTOMATICAMENTE");
        System.out.println((ADICIONAR_METODO_AUXILIAR ? "" : "NÃO ") + "ADICIONAR METODO AUXILIAR");
        System.out.println("--------------------------------------");

        for (String fileAbsolutePath : arquivos) {
            System.out.println("Lendo arquivo: " + fileAbsolutePath);

            System.out.println(">>>>> Processamento em arquivo iniciado");

            Arquivo arquivo = new Arquivo(fileAbsolutePath);
            arquivo.init();

            System.out.println(">>>>> Processamento em arquivo finalizado");
            System.out.println("----------------------------------------------------");
        }
    }
}
