# Bip — Cuidador Digital 📱💊

O **Bip** é um aplicativo Android nativo desenvolvido para otimizar a adesão medicamentosa e o monitoramento clínico. O projeto nasceu originalmente como uma iniciativa de estudantes de Ciência da Computação no Instituto Federal do Ceará (IFCE) e, hoje, passa por um processo profundo de engenharia para **refatorar sua estrutura legada** (anteriormente acoplada em XML e MVP, sem padrões de arquitetura definidos) para um ecossistema moderno, altamente escalável, desacoplado e reativo.

O projeto serve como demonstração prática de Engenharia de Software aplicada à plataforma Android, adotando as diretrizes oficiais de arquitetura da Google, princípios **SOLID**, injeção de dependência nativa e uma interface 100% declarativa.

O grande diferencial de engenharia do Bip é o seu **Agente Reativo Local**: um motor de análise de sintomas baseado em heurísticas que roda de forma determinística e **Offline-First**, tratando dados de saúde sensíveis diretamente no dispositivo com máxima privacidade.

---

## 📐 Engenharia de Software & Arquitetura

A modernização do app eliminou por completo a rigidez do antigo padrão baseado em contratos de View/Presenter, reestruturando o código sob o padrão **MVVM (Model-View-ViewModel)** aliado às diretrizes de **Clean Architecture** e ao fluxo unidirecional de dados (**UDF**).

O projeto adota o isolamento por **features** no nível de apresentação, mantendo os núcleos de negócio e utilitários compartilhados de forma centralizada.

Cada funcionalidade do app respeita rigorosamente a divisão em camadas:

* **Data Layer (`.../data/`):** Implementa o *Repository Pattern* como única fonte de verdade (*Single Source of Truth*). Os dados locais mapeados no Room (`Dao`, `Entity`) e as fontes remotas do Firebase operam em pipelines assíncronos e desacoplados via Mappers.
* **Domain Layer (`.../domain/`):** O coração agnóstico do app escrito em Kotlin puro, livre de dependências do framework Android. Abriga os modelos de negócio puros e os *Use Cases* (Casos de Uso) isolados (cumprindo o Princípio de Responsabilidade Única - **SRP**).
* **Presentation Layer (`.../presentation/`):** Camada puramente declarativa e reativa. As **ViewModels** interceptam os eventos da tela (`UiEvent`), processam as chamadas aos casos de uso e expõem estados imutáveis através de `StateFlow`. A interface construída em **Jetpack Compose** consome esses estados e se auto-reconfigura dinamicamente (Recomposição).

---

## 🛠️ Tech Stack

* **UI & Componentes:** **Jetpack Compose**, utilizando o paradigma declarativo para construção de interfaces modernas e fluidas, com gerenciamento de estado integrado ao ciclo de vida e **Type-Safe Navigation** (Navegação Segura de Tipos via objetos/data classes) estruturada em grafos tipados (`NavGraph`).
* **Injeção de Dependência:** **Hilt (Dagger)**, garantindo inversão de controle (**IoC**) nativa com escopos bem definidos em tempo de compilação.
* **Asincronismo & Reatividade:** Kotlin Coroutines e Kotlin Flow (`StateFlow`), estruturando pipelines reativos de dados do banco até o pixel da tela.
* **Persistência Local:** Room Database, gerenciando o mapeamento relacional offline de medicamentos e o histórico clínico de sintomas.
* **Serviços de Background:** `AlarmManager` integrado a `BroadcastReceiver`, utilizando `setExactAndAllowWhileIdle` para garantir a precisão cirúrgica de alarmes sob as restrições severas do *Doze Mode* do Android.
* **Nuvem & Sincronização:** Firebase (Auth e Firestore) operando em fluxo assíncrono para backup e recuperação segura de perfis.

---

## 🧠 Agente Inteligente & Motor de Regras (IA Local)

Alinhado ao requisito de **IA Local e Confiabilidade**, o módulo `wellbeing` processa de forma 100% offline um agente heurístico local:

1. **Análise de Critério Lógico:** Varredura retroativa na intensidade e notas dos sintomas preenchidos pelo usuário através dos componentes da escala emocional.
2. **Detecção de Padrão Temporal:** Monitoramento direto via `MoodDao` de ocorrências consecutivas de sintomas idênticos em janelas de tempo customizáveis.
3. **Classificação Automática de Risco:** Rotulação de estado do usuário em níveis de atenção (*Estável, Alerta ou Risco Elevado*), chaveando o estado reativo da interface para alterar as expressões do mascote e liberar reativamente as travas de acionamento do módulo de segurança (SOS).

---

## 🧪 Estratégia de Testes

A arquitetura altamente desacoplada via Hilt e Clean Arch permite a validação isolada do comportamento do sistema sem dependências rígidas de emuladores:

* **Testes Unitários (JUnit + MockK):** Cobertura focada na lógica de processamento do motor heurístico, nas validações de regras dos *Use Cases* e nas máquinas de estados testadas diretamente nas ViewModels.
* **Testes Instrumentais de UI (Compose Test Rule):** Execução automatizada e testes de caixa-preta em fluxos críticos, simulando a jornada do usuário do cadastro de medicações ao disparo de alertas.

---

## ⚙️ Status do Projeto & Execução

O projeto encontra-se em **fase final de desenvolvimento, fechamento de features e estabilização**.

O ambiente utiliza o sistema de compilação Gradle configurado em Kotlin DSL (`build.gradle.kts`) e pode ser importado para o Android Studio:
1. Clone o repositório.
2. Execute a sincronização do Gradle.
3. Rode o app através da run configuration padrão para emulador ou dispositivo físico.

---
## 📄 Licença

Distribuído sob os termos da Apache License (Version 2.0). Veja o arquivo `LICENSE` para mais detalhes.

Desenvolvido por **Pedro Henrique Araújo de Oliveira** 🚀