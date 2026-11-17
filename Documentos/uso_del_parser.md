ResultadoParser sea una utilidad de la capa de servicio: 
recibe un String, interpreta el formato y devuelve un ResultadoParseado

ResultadoParseado es objeto inmutable que representa el resultado ya interpretado.  
Por ejemplo:
- Un resultado numérico tendrá valor y posiblemente menorQue.
- Un resultado cualitativo tendrá presencia.
- Los campos que no correspondan quedarán en null o false.

ErrorFormatoResultadoException representa un error esperado: 
el usuario ingresó un texto que no podemos interpretar.

ResultadoParser es la que se encarga de: 
- Normalizar antes de interpretar
- Comprueba primero los resultados cualitativos
- No permite valores numéricos infinitos ni inválidos

Y ParserProvider se encarga de administrar y proporciona la instancia
siendo asi una única instancia compartida evitamos instanciar el parser en cada controlador
haciendo que sea seguro reutilizarlo desde distintos controladores.

Ejemplos de uso:
Podemos probarlo rápidamente desde cualquier clase:
```
ResultadoParser parser = ParserProvider.getResultadoParser();

ResultadoParseado resultado = parser.parse("0,07");

System.out.println(resultado.valor());     // 0.07
System.out.println(resultado.menorQue());  // false
System.out.println(resultado.presencia()); // null
```

Cómo lo vamos a usar en los controladores:  
No necesitamos que cada controlador conozca cómo interpretar los resultados 

Por ejemplo, un formulario con un TextField y un Label para mostrar errores
```
private final ResultadoParser resultadoParser =
        ParserProvider.getResultadoParser();

private ResultadoParseado obtenerResultado() {

    try {
        lblErrorResultado.setText("");

        return resultadoParser.parse(txtResultado.getText());

    } catch (ErrorFormatoResultadoException e) {

        lblErrorResultado.setText(e.getMessage());

        return null;
    }
}
```

Cuando necesitemos guardar el formulario:
```
private void guardarResultado() {

    ResultadoParseado parseado = obtenerResultado();

    if (parseado == null) {
        return;
    }

    ResultadoRequest request = new ResultadoRequest(
            idAnalisis,
            horaControl,
            parseado.valor(),
            parseado.menorQue(),
            null,
            parseado.presencia(),
            false,
            observacion
    );

    // Continuar construyendo el registro...
}
```
En este ejemplo colonias queda en null porque el parser no interpreta ese dato.  
