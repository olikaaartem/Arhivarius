/* =========================================================
   АРХІВ БАНКІВСЬКИХ ІНЦИДЕНТІВ
   ЄДИНИЙ CLEAN script.js
========================================================= */


/* =========================================================
   НАВІГАЦІЯ МІЖ СТОРІНКАМИ
========================================================= */

/**
 * Ховає всі сторінки та показує потрібну.
 * num = номер screen, наприклад 1,2,3...
 */
function goToScreen(num) {
  document.querySelectorAll(".page").forEach(page => {
    page.style.display = "none";
  });

  const target = document.getElementById("screen" + num);

  if (target) {
    target.style.display = "block";
    window.scrollTo(0, 0);
  }
}

/**
 * Старт гри — перехід із обкладинки на Том 1
 */
function startCase() {
  goToScreen(2);
}


/* =========================================================
   POPUP-ПОВІДОМЛЕННЯ
========================================================= */

/**
 * Показує коротке службове повідомлення у правому верхньому куті
 */
function showPopup(message = "ДОСТУП НАДАНО") {
  const oldPopup = document.querySelector(".access-popup");
  if (oldPopup) oldPopup.remove();

  const popup = document.createElement("div");
  popup.className = "access-popup";
  popup.textContent = message;
  document.body.appendChild(popup);

  setTimeout(() => {
    popup.remove();
  }, 2200);
}


/* =========================================================
   ЗАГАЛЬНЕ ПРАВИЛО ДЛЯ ПОЛІВ ВВОДУ
   Усе автоматично у ВЕРХНІЙ РЕГІСТР
========================================================= */

document.addEventListener("input", function (e) {
  const target = e.target;

  if (
    target.matches('input[type="text"]') ||
    target.matches("textarea")
  ) {
    target.value = target.value.toUpperCase();
  }
});


/* =========================================================
   ПІДКАЗКИ
========================================================= */

/**
 * Відкриває / закриває просту підказку
 */
function toggleHint(id) {
  const el = document.getElementById(id);
  if (!el) return;

  if (el.style.display === "block") {
    el.style.display = "none";
  } else {
    el.style.display = "block";
  }
}


/* =========================================================
   ТОМ 1 / ВТРАЧЕНІ КООРДИНАТИ
========================================================= */

/**
 * Правильні класифікації по кейсах
 */
const tom1Answers = {
  1: "КРИЗА",
  2: "ПАНІКА",
  3: "АТАКА",
  4: "ШАХРАЙСТВО",
  5: "ПОГРАБУВАННЯ"
};

/**
 * Множина вже правильно вирішених файлів,
 * щоб не рахувати один файл кілька разів
 */
const tom1Done = new Set();

/**
 * Перевірка однієї відповіді в Томі 1
 */
function checkSingleCase(num) {
  const input = document.getElementById("q" + num);
  const status = document.getElementById("status" + num);
  const story = document.getElementById("story" + num);

  if (!input || !status) return;

  const val = input.value.trim().toUpperCase();

  // Скидаємо попередній стан поля
  input.classList.remove("correct-input", "wrong-input");

  if (val === tom1Answers[num]) {
    status.innerHTML = "✔";
    status.style.color = "#9be28f";
    input.classList.add("correct-input");

    // Показуємо блок “Що сталося”
    if (story) {
      story.style.display = "block";
    }

    tom1Done.add(num);

    // Якщо всі 5 кейсів розв'язані — відкриваємо фінальний блок
    if (tom1Done.size === 5) {
      const ready = document.getElementById("tom1Ready");
      if (ready) ready.style.display = "block";
      showPopup();
    }
  } else {
    status.innerHTML = "✖";
    status.style.color = "#ff6b6b";
    input.classList.add("wrong-input");
  }
}

/**
 * Перевірка фінального слова Тома 1
 */
function checkTom1Code() {
  const input = document.getElementById("codeInputTom1");
  const status = document.getElementById("tom1CodeStatus");
  const nextBtn = document.getElementById("tom1NextBtn");

  if (!input || !status || !nextBtn) return;

  const val = input.value.trim().toUpperCase();

  if (val === "ЗАГРОЗА") {
    status.innerHTML = "✔ ДОСТУП НАДАНО";
    status.style.color = "#9be28f";

    nextBtn.disabled = false;
    nextBtn.classList.remove("disabled-btn");
    nextBtn.onclick = () => goToScreen(3);

    showPopup();
  } else {
    status.innerHTML = "✖ НЕВІРНО";
    status.style.color = "#ff6b6b";
  }
}


/* =========================================================
   ТОМ 2 / РОЗШИФРУЙ ІСТИНУ
========================================================= */

/**
 * Підміна латинських схожих букв на кирилицю,
 * щоб не було проблем із розкладкою
 */
function convertLatinLookalikesToCyrillic(value) {
  const map = {
    A: "А", B: "В", C: "С", E: "Е", H: "Н", I: "І", K: "К", M: "М", O: "О", P: "Р", T: "Т", X: "Х", Y: "У",
    a: "а", b: "в", c: "с", e: "е", h: "н", i: "і", k: "к", m: "м", o: "о", p: "р", t: "т", x: "х", y: "у"
  };

  return value.split("").map(ch => map[ch] || ch).join("");
}

/**
 * Нормалізація введеного значення
 */
function normalizeTom2Value(value) {
  return convertLatinLookalikesToCyrillic(
    value.trim().toUpperCase().replace(/\s+/g, "")
  );
}

/**
 * Для полів Тома 2 одразу переводимо введення у кириличний upper case
 */
function attachTom2UppercaseBehavior() {
  const fields = document.querySelectorAll("#screen3 .tom2-uppercase");

  fields.forEach(field => {
    field.addEventListener("input", function () {
      this.value = convertLatinLookalikesToCyrillic(this.value.toUpperCase());
    });
  });
}

/**
 * Перевірка одного слова в Томі 2
 */
function checkTom2FileWord(inputId, correctAnswer, resultId) {
  const input = document.getElementById(inputId);
  const result = document.getElementById(resultId);

  if (!input || !result) return;

  const userValue = normalizeTom2Value(input.value);
  const correctValue = normalizeTom2Value(correctAnswer);

  input.classList.remove("correct-input", "wrong-input");
  input.value = userValue;

  if (userValue === correctValue) {
    result.textContent = "✔";
    result.style.color = "#9be28f";
    input.classList.add("correct-input");
    input.dataset.correct = "true";
    checkTom2Progress();
  } else {
    result.textContent = "✖";
    result.style.color = "#ff6b6b";
    input.classList.add("wrong-input");
    input.dataset.correct = "false";
  }
}

/**
 * Перевіряє, чи завершена одна група слів у файлі
 */
function isTom2GroupComplete(ids) {
  return ids.every(id => {
    const el = document.getElementById(id);
    return el && el.dataset.correct === "true";
  });
}

/**
 * Загальний прогрес Тома 2
 */
function checkTom2Progress() {
  const file1 = isTom2GroupComplete(["f1_1", "f1_2", "f1_3", "f1_4", "f1_5"]);
  const file2 = isTom2GroupComplete(["f2_1", "f2_2", "f2_3", "f2_4", "f2_5"]);
  const file3 = isTom2GroupComplete(["f3_1", "f3_2", "f3_3", "f3_4", "f3_5"]);
  const file4 = isTom2GroupComplete(["f4_1", "f4_2", "f4_3", "f4_4", "f4_5"]);
  const file5 = isTom2GroupComplete(["f5_1", "f5_2", "f5_3", "f5_4", "f5_5"]);

  const f1done = document.getElementById("tom2file1done");
  const f2done = document.getElementById("tom2file2done");
  const f3done = document.getElementById("tom2file3done");
  const f4done = document.getElementById("tom2file4done");
  const f5done = document.getElementById("tom2file5done");

  if (file1 && f1done) f1done.style.display = "block";
  if (file2 && f2done) f2done.style.display = "block";
  if (file3 && f3done) f3done.style.display = "block";
  if (file4 && f4done) f4done.style.display = "block";
  if (file5 && f5done) f5done.style.display = "block";

  if (file1 && file2 && file3 && file4 && file5) {
    const ready = document.getElementById("tom2Ready");
    if (ready) ready.style.display = "block";
    showPopup();
  }
}

/**
 * Перевірка фінального слова Тома 2
 */
function checkTom2Code() {
  const input =
    document.getElementById("codeInputTom2") ||
    document.getElementById("finalInputTom2");

  const status =
    document.getElementById("tom2CodeStatus") ||
    document.getElementById("finalStatusTom2");

  const nextBtn = document.getElementById("tom2NextBtn");

  if (!input || !status || !nextBtn) return;

  const value = normalizeTom2Value(input.value);

  if (value === "АРХІВ") {
    status.textContent = "✔ ДОСТУП НАДАНО";
    status.style.color = "#9be28f";

    nextBtn.disabled = false;
    nextBtn.classList.remove("disabled-btn");
    nextBtn.onclick = () => goToScreen(4);

    showPopup();
  } else {
    status.textContent = "✖ НЕВІРНЕ КОДОВЕ СЛОВО";
    status.style.color = "#ff6b6b";
  }
}

/**
 * Сумісність із поточним HTML
 */
function checkTom2FinalCode() {
  checkTom2Code();
}


/* =========================================================
   ТОМ 3 / КОДУВАННЯ СВІДОМОСТІ
========================================================= */

/**
 * Множина правильних відповідей Тома 3
 */
const tom3CorrectSet = new Set();

/**
 * Мапа файлів — які ID до якого файлу належать
 */
const tom3FileMap = {
  1: ["t3f1w1", "t3f1w2", "t3f1w3", "t3f1w4", "t3f1w5"],
  2: ["t3f2w1", "t3f2w2", "t3f2w3", "t3f2w4", "t3f2w5"],
  3: ["t3f3w1", "t3f3w2", "t3f3w3", "t3f3w4", "t3f3w5"],
  4: ["t3f4w1", "t3f4w2", "t3f4w3", "t3f4w4"],
  5: ["t3f5w1", "t3f5w2", "t3f5w3", "t3f5w4", "t3f5w5"],
  6: ["t3f6w1", "t3f6w2", "t3f6w3", "t3f6w4", "t3f6w5"]
};

/**
 * Перевірка одного слова в Томі 3
 */
function checkTom3Word(inputId, correct, resultId) {
  const input = document.getElementById(inputId);
  const result = document.getElementById(resultId);

  if (!input || !result) return;

  const val = input.value.trim().toUpperCase();
  const answer = correct.trim().toUpperCase();

  input.classList.remove("correct-input", "wrong-input");

  if (val === answer) {
    result.innerHTML = "✔";
    result.style.color = "#9be28f";
    input.classList.add("correct-input");

    input.disabled = true;
    tom3CorrectSet.add(inputId);
  } else {
    result.innerHTML = "✖";
    result.style.color = "#ff6b6b";
    input.classList.add("wrong-input");
    tom3CorrectSet.delete(inputId);
  }

  checkTom3Progress();
}

/**
 * Чи завершений конкретний файл Тома 3
 */
function isTom3FileComplete(fileNumber) {
  const ids = tom3FileMap[fileNumber];
  if (!ids) return false;
  return ids.every(id => tom3CorrectSet.has(id));
}

/**
 * Загальний прогрес Тома 3
 */
function checkTom3Progress() {
  const file1 = isTom3FileComplete(1);
  const file2 = isTom3FileComplete(2);
  const file3 = isTom3FileComplete(3);
  const file4 = isTom3FileComplete(4);
  const file5 = isTom3FileComplete(5);
  const file6 = isTom3FileComplete(6);

  const d1 = document.getElementById("tom3file1done");
  const d2 = document.getElementById("tom3file2done");
  const d3 = document.getElementById("tom3file3done");
  const d4 = document.getElementById("tom3file4done");
  const d5 = document.getElementById("tom3file5done");
  const d6 = document.getElementById("tom3file6done");

  if (file1 && d1) d1.style.display = "block";
  if (file2 && d2) d2.style.display = "block";
  if (file3 && d3) d3.style.display = "block";
  if (file4 && d4) d4.style.display = "block";
  if (file5 && d5) d5.style.display = "block";
  if (file6 && d6) d6.style.display = "block";

  // Фінальний блок відкриваємо після основних 1–5 файлів
  if (file1 && file2 && file3 && file4 && file5) {
    const ready = document.getElementById("tom3Ready");
    if (ready) ready.style.display = "block";
  }
}

/**
 * Перевірка фінального слова Тома 3
 */
function checkTom3Code() {
  const input = document.getElementById("code3");
  const result = document.getElementById("res3");
  const nextBtn = document.getElementById("tom3NextBtn");

  if (!input || !result || !nextBtn) return;

  const val = input.value.trim().toUpperCase();

  if (val === "ПОТІК") {
    result.innerHTML = "✔ ДОСТУП НАДАНО";
    result.style.color = "#9be28f";

    nextBtn.disabled = false;
    nextBtn.classList.remove("disabled-btn");

    showPopup();
  } else {
    result.innerHTML = "✖ НЕВІРНЕ СЛОВО";
    result.style.color = "#ff6b6b";
  }
}


/* =========================================================
   ТОМ 4 / МАТРИЦЯ СИГНАЛІВ
========================================================= */

/**
 * Правильні цифри до загадок
 */
const tom4Answers = [4, 3, 6, 2, 7, 5, 4, 4, 4, 3];

/**
 * Ці самі цифри треба знайти в матриці
 */
const tom4TargetDigits = [...tom4Answers];

/**
 * Масив уже знайдених цифр
 */
let tom4Found = [];

/**
 * Перевірка відповідей до загадок
 */
function checkTom4Inputs() {
  let allCorrect = true;

  for (let i = 1; i <= 10; i++) {
    const input = document.getElementById("tom4n" + i);
    if (!input) continue;

    const val = parseInt(input.value, 10);

    input.classList.remove("correct-input", "wrong-input");

    if (val === tom4Answers[i - 1]) {
      input.classList.add("correct-input");
    } else {
      input.classList.add("wrong-input");
      allCorrect = false;
    }
  }

  const status = document.getElementById("tom4InputsStatus");
  const matrixWrap = document.getElementById("safeMatrixWrap");
  const targetsBox = document.getElementById("tom4TargetsBox");

  if (allCorrect) {
    if (status) {
      status.innerHTML = "✔ ДОСТУП НАДАНО";
      status.style.color = "#9be28f";
    }

    if (matrixWrap) matrixWrap.style.display = "block";
    if (targetsBox) targetsBox.style.display = "block";

    renderTom4Targets();
    buildMatrix();
    showPopup();
  } else {
    if (status) {
      status.innerHTML = "✖ Є ПОМИЛКИ";
      status.style.color = "#ff6b6b";
    }
  }
}

/**
 * Малює зверху цифри, які треба знайти
 */
function renderTom4Targets() {
  const list = document.getElementById("tom4TargetsList");
  if (!list) return;

  list.innerHTML = "";

  tom4TargetDigits.forEach((digit, index) => {
    const item = document.createElement("div");
    item.className = "target-digit";
    item.id = "targetDigit" + index;
    item.textContent = digit;
    list.appendChild(item);
  });
}

/**
 * Генерує матрицю 15x15
 * Усі випадкові цифри — БЕЗ правильних цифр
 * Правильні цифри вставляються лише в потрібній кількості
 */
function buildMatrix() {
  const matrix = document.getElementById("safeMatrix");
  if (!matrix) return;

  matrix.innerHTML = "";
  tom4Found = [];

  const size = 15;
  const total = size * size;
  const numbers = [];
  const usedPositions = new Set();

  // 1. Наповнюємо матрицю цифрами, які НЕ входять у правильну комбінацію
  for (let i = 0; i < total; i++) {
    let num;
    do {
      num = Math.floor(Math.random() * 10);
    } while (tom4TargetDigits.includes(num));

    numbers.push(num);
  }

  // 2. Вставляємо правильні цифри в унікальні позиції
  tom4TargetDigits.forEach(digit => {
    let pos;
    do {
      pos = Math.floor(Math.random() * total);
    } while (usedPositions.has(pos));

    usedPositions.add(pos);
    numbers[pos] = digit;
  });

  // 3. Малюємо кнопки
  numbers.forEach(num => {
    const btn = document.createElement("button");
    btn.className = "safe-cell";
    btn.type = "button";
    btn.textContent = num;
    btn.onclick = () => handleMatrixClick(btn, num);
    matrix.appendChild(btn);
  });

  updateProgress();
}

/**
 * Клік по клітинці матриці
 */
function handleMatrixClick(btn, num) {
  if (tom4TargetDigits.includes(num)) {
    btn.classList.add("correct");

    if (!btn.dataset.clicked) {
      btn.dataset.clicked = "true";

      const targetItems = document.querySelectorAll(".target-digit");
      for (const item of targetItems) {
        if (!item.classList.contains("found") && item.textContent === String(num)) {
          item.classList.add("found");
          tom4Found.push(num);
          break;
        }
      }
    }

    updateProgress();

    if (tom4Found.length === 10) {
      openSafe();
    }
  } else {
    btn.classList.add("wrong-input");
    setTimeout(() => {
      btn.classList.remove("wrong-input");
    }, 400);
  }
}

/**
 * Оновлює лічильник знайдених цифр
 */
function updateProgress() {
  const progress = document.getElementById("safeProgress");
  if (progress) {
    progress.innerText = tom4Found.length + " / 10";
  }
}

/**
 * Відкриває сейф і показує архівний документ
 */
function openSafe() {
  const safeOpened = document.getElementById("tom4SafeOpened");
  const doc = document.getElementById("tom4Doc");

  if (safeOpened) safeOpened.style.display = "block";
  if (doc) doc.style.display = "block";

  document.querySelectorAll(".safe-cell").forEach(btn => {
    btn.classList.add("locked");
  });

  showPopup("ФРАГМЕНТ АРХІВУ ВІДКРИТО");
}

/**
 * Перевірка фінального слова Тома 4
 */
function checkTom4Final() {
  const input = document.getElementById("tom4FinalWord");
  const res = document.getElementById("tom4FinalResult");
  const btn = document.getElementById("tom4NextBtn");

  if (!input || !res || !btn) return;

  const val = input.value.trim().toUpperCase();

  if (val === "ДЖЕРЕЛА") {
    res.innerHTML = "✔ ДОСТУП НАДАНО";
    res.style.color = "#9be28f";

    input.classList.remove("wrong-input");
    input.classList.add("correct-input");

    btn.disabled = false;
    btn.classList.remove("disabled-btn");

    showPopup();
  } else {
    res.innerHTML = "✖ НЕВІРНО";
    res.style.color = "#ff6b6b";

    input.classList.remove("correct-input");
    input.classList.add("wrong-input");
  }
}


/* =========================================================
   ТОМ 5 / ЗУМ КАРТИНКИ
========================================================= */

/**
 * Збільшення / зменшення картинки по кліку
 */
document.addEventListener("click", function (e) {
  if (e.target.classList.contains("osint-img")) {
    e.target.classList.toggle("zoomed");
  }
});


/* =========================================================
   ТОМ 5 / OSINT — ДВІ ОКРЕМІ ПЕРЕВІРКИ
========================================================= */

/**
 * Чи правильний код на ящику
 */
let osintCodeOk = false;

/**
 * Чи правильні координати
 */
let osintCoordsOk = false;

/**
 * Перевіряє, чи вже виконано обидві частини,
 * і якщо так — відкриває фінальний звіт
 */
function updateOsintFinalState() {
  const status = document.getElementById("osintTask1Status");
  const finalReport = document.getElementById("finalReport");
  const nextBtn = document.getElementById("tom5NextBtn");

  if (osintCodeOk && osintCoordsOk) {
    if (status) {
      status.innerHTML = "✔ ДОСТУП НАДАНО";
      status.style.color = "#9be28f";
    }

    if (finalReport) {
      finalReport.style.display = "block";
    }

    if (nextBtn) {
      nextBtn.disabled = false;
      nextBtn.classList.remove("disabled-btn");
    }

    setTimeout(() => {
      const stamp = document.getElementById("finalStamp");
      if (stamp) stamp.classList.add("show");
    }, 300);

    showPopup();
  }
}

/**
 * Перевірка коду на ящику
 */
function checkOsintCode() {
  const input = document.getElementById("osint1a");
  const result = document.getElementById("osintRes1a");

  if (!input || !result) return;

  const value = input.value.trim().toUpperCase();

  input.classList.remove("correct-input", "wrong-input");

  if (value === "340/500") {
    result.innerHTML = "✔";
    result.style.color = "#9be28f";
    input.classList.add("correct-input");
    osintCodeOk = true;
  } else {
    result.innerHTML = "✖";
    result.style.color = "#ff6b6b";
    input.classList.add("wrong-input");
    osintCodeOk = false;
  }

  updateOsintFinalState();
}

/**
 * Перевірка координат
 * Гнучка логіка: не треба абсолютний формат,
 * достатньо наявності всіх потрібних чисел
 */
function checkOsintCoords() {
  const input = document.getElementById("osint1b");
  const result = document.getElementById("osintRes1b");

  if (!input || !result) return;

  const value = input.value.trim().toUpperCase();

  input.classList.remove("correct-input", "wrong-input");

  const coordOk =
    value.includes("50") &&
    value.includes("09") &&
    value.includes("42") &&
    value.includes("6") &&
    value.includes("50") &&
    value.includes("55");

  if (coordOk) {
    result.innerHTML = "✔";
    result.style.color = "#9be28f";
    input.classList.add("correct-input");
    osintCoordsOk = true;
  } else {
    result.innerHTML = "✖";
    result.style.color = "#ff6b6b";
    input.classList.add("wrong-input");
    osintCoordsOk = false;
  }

  updateOsintFinalState();
}


/* =========================================================
   ЕФЕКТ ЗАКРИТТЯ АРХІВУ
========================================================= */

/**
 * Показує кінематографічне закриття архіву
 * і переводить на фінальну сторінку
 */
function closeArchiveAndGoTo7() {
  const oldOverlay = document.querySelector(".archive-overlay");
  if (oldOverlay) oldOverlay.remove();

  const overlay = document.createElement("div");
  overlay.className = "archive-overlay";

  overlay.innerHTML = `
    <div class="archive-cinematic-text">
      <div class="archive-cinematic-line main">СПРАВУ №404 ЗАКРИТО</div>
      <div class="archive-cinematic-line">Сигнал був.</div>
      <div class="archive-cinematic-line">Його просто не побачили вчасно.</div>
      <div class="archive-cinematic-line small">
        Дякуємо за уважність, допитливість і стійкість.
      </div>
    </div>
  `;

  document.body.appendChild(overlay);

  setTimeout(() => {
    overlay.classList.add("show");
  }, 50);

  setTimeout(() => {
    goToScreen(7);
  }, 3200);

  setTimeout(() => {
    overlay.remove();
  }, 3600);
}


/* =========================================================
   ІНІЦІАЛІЗАЦІЯ ПІСЛЯ ЗАВАНТАЖЕННЯ
========================================================= */

/**
 * Після завантаження сторінки
 * активуємо додаткову логіку для Тома 2
 */
document.addEventListener("DOMContentLoaded", function () {
  attachTom2UppercaseBehavior();
});



document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll("input").forEach(input => {
    input.setAttribute("autocomplete", "new-password");
  });
});
