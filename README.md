
# ReplaceProjeto14192
Parser para replace em massa para passagem de novo parâmetro.

# Configurações
   - Para adicionar inject automaticamente **ReplaceProjeto.ADICIONAR_INJECT_ATEPACVARIABLES** (padrão = **true**). Caso **false**, não será adicionado:
```javascript
  @Inject('AtePacEHVariables')
  atePacEHVariables;
```

   - Para adicionar método auxiliar para fazer a chamada posteriormente **ReplaceProjeto.ADICIONAR_METODO_AUXILIAR** (padrão = **true**). Caso **false**, não será adicionado:
```javascript
  getWLDFocused() {
    return this.atePacEHVariables.getCurrentLinkedData();
  }
```

   - Para sobrescrever arquivo original por arquivo modificado **ReplaceProjeto.SOBRESCREVER_ARQUIVO_ORIGINAL** (padrão = **true**). Caso **false**, irá salvar o arquivo com mesmo nome no diretório **C:/**.

   - Para escolher os arquivos onde será feito as modificações, basta alterar o array **ReplaceProjeto.FILES**.

# Exceções
Caso na mesma linha do pattern ```.events.``` exista as seguintes chamadas, o evento não será modificado:
   - getAttribute 
   - cleanListeners
   - fireDeferredEvent
   - fireEvent
   - fireEventAsync
   - getListeners
   - removeListener

# Observações
Verificar manualmente classe que possuir ```Eventos ignorados > 0``` no log parar se certificar que eventos foram ignorados por conta das ```Exceções``` e não por conta de mal funcionamento do Parser.

# Exemplos
Única linha:
```javascript
//ANTES
detail.events.addAttribChange(['ATRIBUTO'], () => this.method(detail));

//DEPOIS
detail.events.addAttribChange(['ATRIBUTO'], () => this.method(detail), this.getWLDFocused());
```

```javascript
//ANTES
detail.events.addAttribFocus(() => this.method(schematics));

//DEPOIS
detail.events.addAttribFocus(() => this.method(schematics), this.getWLDFocused());
```

Multiplas linhas:
```javascript
// ANTES
    detail.events.addAttribChange(['ATRIBUTO'], () => {
      this.method(detail, 'ATRIBUTO');
    }, this.getWLDFocused());

//DEPOIS
    detail.events.addAttribChange(['ATRIBUTO'], () => {
      this.method(detail, 'ATRIBUTO');
    });
```

Caso com ocorrência de ```});``` também irá funcionar.
```javascript
//ANTES
    detail.events.addAttribChange(['ATRIBUTO_1'], evt => {
      this.objeto.metodo1(evt.oldValue);
      this.objeto.metodo2(detail, dbPanel, 'ATRIBUTO_2', 'ATRIBUTO_3');
      this.executeSomething('aaaaaa', {}); // Exemplo finalizado em });
      this.executeSomething('aaaaaa', {}); // Exemplo finalizado em });
    });

//DEPOIS
    detail.events.addAttribChange(['ATRIBUTO_1'], evt => {
      this.objeto.metodo1(evt.oldValue);
      this.objeto.metodo2(detail, dbPanel, 'ATRIBUTO_2', 'ATRIBUTO_3');
      this.executeSomething('aaaaaa', {}); // Exemplo finalizado em });
      this.executeSomething('aaaaaa', {}); // Exemplo finalizado em });
    }, this.getWLDFocused());
```

Caso sem definição de array com atributos ['....'].
```javascript
    //ANTES
    detail.events.addAttribChange(() => {
      this.method(detail, 'ATRIBUTO');
    });

    //DEPOIS
    detail.events.addAttribChange(() => {
      this.method(detail, 'ATRIBUTO');
    }, this.getWLDFocused());
```
