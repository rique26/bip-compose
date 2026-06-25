# 📱 Bip — Cuidador Digital

O **Bip** é um aplicativo Android nativo desenvolvido para auxiliar na adesão a medicamentos e no monitoramento clínico de bem-estar. O projeto nasceu originalmente como uma iniciativa acadêmica no Instituto Federal do Ceará (IFCE) e, hoje, passa por um processo de engenharia focado no desacoplamento de suas regras de negócio e modernização da interface.

O projeto adota as diretrizes oficiais de arquitetura do Google (**Guide to App Architecture**), aplicando **Clean Architecture**, princípios **SOLID** e funcionamento **Offline-First**.

---

## 📐 Estrutura do Projeto & Arquitetura

A arquitetura do projeto utiliza o fluxo unidirecional de dados (**UDF**) aliado ao padrão **MVVM** para gerenciamento de estados imutáveis na interface. A estrutura é dividida por pacotes de funcionalidades (*Feature-Driven*), onde cada módulo respeita a separação de conceitos em camadas limpas:

* **Data (`.../data/`):** Gerencia as fontes de dados locais (**Room Database**) e remotas (**Firebase Firestore**), utilizando o padrão de repositórios como fonte única de verdade e isolando as entidades através de mappers.
* **Domain (`.../domain/`):** Escrito em Kotlin puro, sem dependências do framework do Android. Contém os modelos de negócio e os Casos de Uso (*Use Cases*) isolados por responsabilidade única.
* **Presentation (`.../presentation/`):** Camada de interface construída em **Jetpack Compose**. As ViewModels interceptam os eventos da tela (`UiEvent`), interagem com a camada de domínio e expõem o estado para a UI via `StateFlow`.

---

## 🛠️ Tech Stack

* **UI & Navegação:** Jetpack Compose (paradigma 100% declarativo) com **Type-Safe Navigation** estruturada em grafos tipados (`NavGraph`) usando objetos e data classes.
* **Injeção de Dependência:** Hilt (Dagger), garantindo o gerenciamento nativo de dependências escopadas por camadas.
* **Asincronismo:** Kotlin Coroutines e Kotlin Flow (`StateFlow`, `SharedFlow`).
* **Persistência Local:** Room Database para armazenamento offline de medicamentos e logs de sintomas.
* **Serviços de Background:** `AlarmManager` combinado com `BroadcastReceiver`, utilizando `setExactAndAllowWhileIdle` para garantir a execução precisa de alarmes de medicamentos sob as restrições do *Doze Mode*.
* **Backend & Autenticação:** Firebase Auth e Firestore para sincronização opcional de perfis em nuvem.

---

## 🧠 Módulo de Análise Local (Módulo Wellbeing)

O módulo `wellbeing` processa o histórico de logs do usuário localmente de forma offline:

1. **Leitura de Dados:** O sistema consulta o histórico de sintomas e notas inseridos na tela através do `MoodDao`.
2. **Análise de Janela Temporal:** Identifica a recorrência ou persistência de sintomas específicos em períodos selecionados pelo usuário.
3. **Classificação de Estado:** Mapeia os dados em níveis de atenção (*Estável, Alerta ou Risco Elevado*), atualizando o estado visual da interface (como as expressões do mascote do app) e gerenciando as travas de acionamento do módulo de segurança (SOS).

---

## 🧪 Estratégia de Testes

A separação de conceitos através da Clean Architecture permite validar as regras de negócio de forma isolada:

* **Testes Unitários (JUnit + MockK):** Focados nas validações de regras dos *Use Cases*, no processamento de dados do histórico e nas máquinas de estado das ViewModels.
* **Testes Instrumentais (Compose Test Rule):** Testes de interface automatizados simulando fluxos críticos, como o cadastro de novos medicamentos.

---

## 🏁 Status do Projeto

O **Bip** encontra-se atualmente em fase de **refatoração estrutural**. O objetivo principal do estágio atual é migrar o código legado — originalmente construído de forma acoplada em XML e arquitetura MVP, sem padrões definidos de SOLID ou Clean Arch — para o ecossistema moderno em **Jetpack Compose** e **Hilt**.

As funcionalidades fundamentais de persistência em Room, alarmes em background e as telas reativas do módulo de histórico (`wellbeing`) com gráficos já foram completamente migradas para o novo padrão.

---

## ⚙️ Como Rodar o Projeto

1. Certifique-se de utilizar o **Android Studio Ladybug** (ou superior).
2. Clone o repositório.
3. Abra o projeto no Android Studio e aguarde a sincronização do Gradle (`build.gradle.kts`).
4. Execute o aplicativo em um emulador ou dispositivo físico.