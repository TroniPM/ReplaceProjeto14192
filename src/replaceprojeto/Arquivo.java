package replaceprojeto;

import java.util.Random;
import static replaceprojeto.ReplaceProjeto.ADICIONAR_INJECT_ATEPACVARIABLES;
import static replaceprojeto.ReplaceProjeto.ADICIONAR_METODO_AUXILIAR;
import static replaceprojeto.ReplaceProjeto.PATTERN_EVENTS;
import static replaceprojeto.ReplaceProjeto.PATTERN_LINKEDDATA_INLINE;
import static replaceprojeto.ReplaceProjeto.PATTERN_LINKEDDATA_MULTIPLELINE;
import static replaceprojeto.ReplaceProjeto.SOBRESCREVER_ARQUIVO_ORIGINAL;

/**
 *
 * @author pmdsilva
 * @date 02-mar-2021
 */
public class Arquivo {

    public int EVENTS_COUNT = 0;
    public int EVENTS_INLINE_COUNT = 0;
    public int EVENTS_MULTIPLELINE_COUNT = 0;

    public String fileAbsolutePath = null;
    public String arquivo = null;

    public Arquivo(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        this.arquivo = IO.read(fileAbsolutePath);
    }

    public void init() {
        arquivo = checarArquivoPorPatternEventos(arquivo);

        if (EVENTS_COUNT == 0) {
            System.out.println("Não há eventos no arquivo. Não será sobrescrito.");
        } else {
            System.out.println("Eventos encontrados: " + EVENTS_COUNT);
            System.out.println("Eventos inline: " + EVENTS_INLINE_COUNT);
            System.out.println("Eventos multiplelines: " + EVENTS_MULTIPLELINE_COUNT);

            boolean hasInject = procurarPorInject(arquivo);
            System.out.println("Arquivo" + (hasInject ? " " : " NÃO ") + "possui inject do AtePacEHVariables. " + (ADICIONAR_INJECT_ATEPACVARIABLES && !hasInject ? "Adicionando..." : ""));

            if (ADICIONAR_INJECT_ATEPACVARIABLES) {
                int first = arquivo.indexOf("export default class");
                int chaves = arquivo.indexOf("{", first);
                String definicaoClasse = arquivo.substring(first, chaves + 1);

                String replace = "\r\n  @Inject('AtePacEHVariables')\r\n  atePacEHVariables;";

                arquivo = arquivo.replace(definicaoClasse, definicaoClasse + "\r\n" + replace);
            }

            if (ADICIONAR_METODO_AUXILIAR) {
                String metodo = getMetodoAuxiliar();
                arquivo = arquivo.trim();
                arquivo = arquivo.substring(0, arquivo.length() - 2); //removendo ultima }
                arquivo = arquivo.trim();
                arquivo = arquivo.substring(0, arquivo.length() - 2); //removendo penultima }

                arquivo = arquivo + " }\r\n\r\n" + metodo + "\r\n}\r\n";
            }

            System.out.println(">>>>> Salvando arquivo");
            String outputNamePath;

            if (SOBRESCREVER_ARQUIVO_ORIGINAL) {
                outputNamePath = fileAbsolutePath;
            } else {
                String[] palavras = fileAbsolutePath.contains("\\") ? fileAbsolutePath.split("\\\\") : fileAbsolutePath.split("/");
                String name = "C:/" + palavras[palavras.length - 1];
                outputNamePath = name;
            }
            System.out.println("Caminho de saída: " + outputNamePath);
            IO.write(outputNamePath, arquivo, false);
        }

        EVENTS_COUNT = 0;
        EVENTS_INLINE_COUNT = 0;
        EVENTS_MULTIPLELINE_COUNT = 0;
    }

    private String checarArquivoPorPatternEventos(String arquivo) {
        int lastIndex = 0;
        while (lastIndex != -1) {

            lastIndex = arquivo.indexOf(PATTERN_EVENTS, lastIndex);

            if (lastIndex != -1) {
                EVENTS_COUNT++;
                lastIndex += PATTERN_EVENTS.length();
                arquivo = changeParaEvento(arquivo, lastIndex);
            }
        }

        return arquivo;
    }

    private String gerarNomeArquivo() {
        Random r = new Random();
        int randomInt = r.nextInt(100000000) + 1;

        return "C:/" + randomInt + "_replace.txt";
    }

    private String changeParaEvento(String arquivo, int eventIndex) {
        int newLineR = arquivo.substring(eventIndex).indexOf("\r");
        newLineR = newLineR == - 1 ? Integer.MAX_VALUE : newLineR;
        int newLineN = arquivo.substring(eventIndex).indexOf("\n");
        newLineN = newLineN == - 1 ? Integer.MAX_VALUE : newLineN;
        int posicaoProximaLinha = Math.min(newLineR, newLineN);

        int posicaoPontoVirgula = arquivo.substring(eventIndex).indexOf(";");

        if (posicaoPontoVirgula < posicaoProximaLinha
                && (!arquivo.substring(eventIndex).substring(0, posicaoPontoVirgula + 1).contains("{"))
                && !arquivo.substring(eventIndex).substring(0, posicaoPontoVirgula + 1).contains("}")) { //INLINE
            EVENTS_INLINE_COUNT++;
            arquivo = schemeInline(arquivo, eventIndex, posicaoPontoVirgula);
        } else {//MULTIPLELINES
            EVENTS_MULTIPLELINE_COUNT++;
            arquivo = schemeMultipleLines(arquivo, eventIndex, 0);
//            String STR_aPartirDoEvento = arquivo.substring(eventIndex);
//            IO.write(gerarNomeArquivo(), STR_aPartirDoEvento, false);
        }

        return arquivo;
    }

    public String schemeInline(String arquivo, int eventIndex, int posicaoPontoVirgula) {
        String target = arquivo.substring(eventIndex).substring(0, posicaoPontoVirgula + 1);

        int ultimoParentese = target.lastIndexOf(")");
        String newText = target.substring(0, ultimoParentese) + PATTERN_LINKEDDATA_INLINE + target.substring(ultimoParentese + 1, target.length());
//            System.out.println("ANTES: " + target);
//            System.out.println("DEPOIS: " + newText); 

        return arquivo.replace(target, newText);
    }

    public String schemeMultipleLines(String arquivo, int eventIndex, int endingIndex) {
        int endingPattern = arquivo.substring(eventIndex).indexOf("});", endingIndex);

        String definicao = arquivo.substring(eventIndex).substring(0, endingPattern + 3);

        int countAnexadores = 0; // ( ) [ ] { }
        int anexadorPAberto = definicao.length() - definicao.replace("(", "").length();
        int anexadorPFechado = definicao.length() - definicao.replace(")", "").length();
        int anexadorCAberto = definicao.length() - definicao.replace("[", "").length();
        int anexadorCFechado = definicao.length() - definicao.replace("]", "").length();
        int anexadorChAberto = definicao.length() - definicao.replace("{", "").length();
        int anexadorChFechado = definicao.length() - definicao.replace("}", "").length();

        countAnexadores = anexadorPAberto + anexadorPFechado + anexadorCAberto + anexadorCFechado + anexadorChAberto + anexadorChFechado;

        if (anexadorPAberto == anexadorPFechado
                && anexadorCAberto == anexadorCFechado
                && anexadorChAberto == anexadorChFechado) {
//            System.out.println("Anexadores: " + countAnexadores);

            String target = arquivo.substring(eventIndex).substring(0, endingPattern + 3);

            int start = target.lastIndexOf("});");
//            String toReplace = "}" + PATTERN_LINKEDDATA_MULTIPLELINE + ";";;
            String newText = target.substring(0, start) + PATTERN_LINKEDDATA_MULTIPLELINE + target.substring(start + 3);

//            System.out.println("TARGET: " + target);
//            System.out.println("REPLACE: " + newString);
            return arquivo.replace(target, newText);
        } else {
//            System.out.println("Anexadores: " + countAnexadores);;
//            System.out.println("@@@@@@@@@ Chamando novamente schemeMultipleLines() por desbalanceamento de anexadores.");
            return schemeMultipleLines(arquivo, eventIndex, endingPattern + 3);
        }

//        return arquivo;
    }

    public boolean procurarPorInject(String arquivo) {
        boolean flag = arquivo.contains("@Inject('AtePacEHVariables')");
        return flag;
    }

    public String getMetodoAuxiliar() {
        String aux
                = "  getWLDFocused() {\r\n"
                + "    return this.atePacEHVariables.getCurrentLinkedData();\r\n"
                + "  }\r\n";

        return aux;
    }
}
