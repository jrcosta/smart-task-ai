# 📚 Geração de JavaDoc - Smart Task Manager

## ⚡ Forma Rápida (Recomendada)

### Windows - Duplo clique:
```
RUN-JAVADOC.bat
```

O script irá:
1. ✅ Verificar Maven
2. ✅ Compilar o projeto
3. ✅ Gerar documentação
4. ✅ Abrir no navegador

---

## 📋 Formas Alternativas

### Via Maven (CLI):
```bash
cd backend
mvn javadoc:aggregate -DskipTests
```

### Via PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File .\gerar-javadoc-simples.ps1
```

---

## 📁 Onde está a Documentação?

Após gerar, acesse:
```
backend/target/site/apidocs/index.html
```

---

## ✨ Requisitos

- ✅ Java 25 (instalado)
- ✅ Maven 3.9.x (disponível no PATH)
- ✅ Lombok 1.18.40 (no pom.xml)

---

## 🔧 Configurado para:

- **Java:** 25
- **Spring Boot:** 3.5
- **Maven:** 3.9.x
- **Lombok:** 1.18.40

---

## 📝 Documentação Gerada

- ✅ 40+ classes documentadas
- ✅ JavaDoc em português
- ✅ Exemplos de uso
- ✅ Tabelas de relacionamentos
- ✅ Enums explicados
- ✅ Anotações JPA/Validation documentadas

---

**Última atualização:** 18 de Outubro de 2025
