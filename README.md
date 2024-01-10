# Arquivo de Base

<!-- TOC -->

* [Arquivo de Base](#arquivo-de-base)
    * [Nomenclaturas](#nomenclaturas)
        * [Obs: `Nunca usar snake_case, pfvrzin`](#obs-nunca-usar-snakecase-pfvrzin)
    * [Pacotes](#pacotes)
        * [Nome](#nome)
            * [Exemplo `service.context` ou `servicecontext`](#exemplo-servicecontext-ou-servicecontext)
        * [Sobre os pacotes](#sobre-os-pacotes)

<!-- TOC -->

## Java

### Estruturas de dado

O java já deixa pronto pra gente inúmeras implementações de todas as estruturas, como, por exemplo:

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        List<String> list2 = new LinkedList<>();
        List<String> list3 = new Stack<>();
        List<String> list4 = new Vector<>();
    }
}
```

Mas repara que na hora de declarar, eu uso `List<String> list = new ArrayList<>();`,
não `ArrayList<String> list = new ArrayList<>();`. A ideia é que, ao declarar pela interface mais genérica,
em um parâmetro de um método, por exemplo, caso a gente troca a implementação da lista, a gente não precisa refatorar o
código todo. O mesmo vale para Maps (dicionário do python, geralmente a gente usa o HashMap), Sets (HashSet), etc.

Exemplo:

```java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        // Pro método não importa qual implementação de lista a gente ta usando

        this.computarLista(new ArrayList<>());
        this.computarLista(new LinkedList<>());
        this.computarLista(new Stack<>());
    }

    private void computarLista(List<String> lista) {
        // Fazer alguma coisa com a lista
    }
}
```

## Nomenclaturas

```java
public class PascalCase {
    private final String camelCase = "";

    private void camelCase() {
        boolean camelCase = true;
    }
}
```

##### Obs: `Nunca usar snake_case, pfvrzin`

## Pacotes

### Nome

Eu tenho a mania de dividir múltiplas palavras no nome do pacote com `.`  mas não é errado concatenar as palavras no
nome de pacote

###### Exemplo `service.context` ou `servicecontext`

### Sobre os pacotes

Provavelmente na aplicação agora a gente só vai ter que mexer principalmente em 3 pacotes:

`controler`

- Pacote que contém os `controllers`, classes que controlam os endpoints (qual a rota, o método, parâmetros, etc.)

`service`

- Pacote que contém os `services`, classes que contém todas as lógicas de negócio, talvez quando a aplicação crescer
  pode fazer sentido a gente dividir esse em sub-pacotes para diferentes entidades/use-cases (user, email, auth), etc.

`domain`

- Nesse pacote tem tudo do domínio da aplicação, divido em sub-pacotes. Por exemplo, no pacote `domain.user`, você vai
  achar os pacotes:
    - `dto` que contém todos os dto (classe pra definir o tipo do corpo da requisição, ou resposta, por
      exemplo)
    - `model` com o model do usuário, a entidade em sí salva no banco. **Nunca retornar diretamente
      uma entidade em uma rota, sempre usar DTOs para garantir que não retorna informações sensíveis, tipo senha**
    - `repository` com todas as classes de repositório usadas pela entidade.
        - Por exemplo, um repo de cache e outro do jpa (banco de dados), e uma classe em sí de Repositório, a
          classe usada pela aplicação, que vai centralizar a lógica (usar db X cache, por exemplo).
        - Para entender melhor, olha
          em [UserRepository](src/main/java/andre/chamis/healthproject/domain/user/repository/UserRepository.java)