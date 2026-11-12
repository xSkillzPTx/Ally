# Ally
Trabalho prático da unidade curricular de **Computação Distribuída**, 
da licenciatura em Engenharia Informática no Instituto Politécnico de 
Tomar, no ano letivo 2026/27.

**Verificação de proprietários e contratos de arrendamento de férias** 
com *Blockchain ID*. Antes de pagar, o turista confirma que quem
anuncia a casa é o verdadeiro proprietário, e o contrato fica registado
de forma inalterável.

### O problema
Todos os verões multiplicam-se as burlas com casas de férias, acontece da
seguinte forma, o burlão anuncia uma casa que não é dele, 
pede um sinal por transferência e, quando o turista chega, a casa não 
existe ou é de outra pessoa. Os documentos que provam a propriedade 
(e.g. caderneta predial, certidão permanente, escritura) expõem 
demasiados dados pessoais para serem mostrados a desconhecidos.

### Como é que usamos a blockchain?
1. **Emissão da credencial:** uma entidade emissora (as Finanças, simuladas no protótipo) confirma que o proprietário é titular do imóvel e emite uma credencial assinada: *"a identidade digital X é titular do imóvel Y"*.
2. **Publicação do anúncio:** o proprietário assina o anúncio com a sua identidade digital.
3. **Verificação pelo turista:** com um código QR ou um código, o turista confirma na blockchain que a credencial é válida e não foi revogada.
4. **Prova de posse:** o turista envia um desafio aleatório que só o verdadeiro dono consegue assinar com a sua chave privada. Copiar o QR de outro anúncio não chega.
5. **Contrato:** proprietário e turista assinam o contrato e o seu *hash* fica registado na blockchain.
6. **Revogação:** se o imóvel for vendido, o emissor revoga a credencial e os anúncios deixam de ser válidos.

## Autores

| Nº    | Nome         |
|-------|--------------|
| 26165 | Igor Fonseca |
| 27502 | Afonso Viana |

## Intervenientes
- **Emissor:** verifica os documentos e emite e revoga credenciais de propriedade
- **Titular (proprietário):** tem a identidade digital, guarda as credenciais e assina anúncios e contratos
- **Verificador (turista):** confirma a credencial e a identidade do anunciante sem ter de confiar em ninguém

## Funcionalidades
- Identidades digitais baseadas em pares de chaves RSA
- Emissão, verificação e revogação de credenciais de propriedade
- Anúncios assinados pelo proprietário
- Prova de posse por desafio-resposta
- Registo de contratos assinados por ambas as partes
- Interface gráfica para cada interveniente
- Explorador da blockchain (blocos, hashes e *nonce*)

## Privacidade e RGPD
Segue o princípio da **minimização de dados**: o turista nunca vê os documentos, só a conclusão assinada pelo emissor.

| Na blockchain (*on-chain*) | Fora da blockchain (*off-chain*) |
|---|---|
| Identificadores e chaves públicas das identidades digitais | Documentos apresentados ao emissor |
| Hashes dos identificadores dos imóveis | Dados pessoais (nome, NIF, morada) |
| Hashes dos contratos e assinaturas digitais | Conteúdo integral dos contratos |
| Datas e estados de revogação das credenciais | |

## Tecnologias
- Java e Swing (Apache NetBeans)
- SHA3-256 para hashes e ligação entre blocos
- Assinaturas digitais SHA3-256withRSA
- Proof of Work com mineração em várias *threads*
- Árvores de Merkle
- Rede *peer-to-peer* entre vários nós

## Organização do trabalho
- `main`: versão estável
- `feature/<nome>`: uma branch por funcionalidade, integrada por pull request
