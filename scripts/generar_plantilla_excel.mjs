import fs from "node:fs/promises";
import path from "node:path";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

const outputDir = path.resolve("outputs");
await fs.mkdir(outputDir, { recursive: true });

const workbook = Workbook.create();
const sheet = workbook.worksheets.add("Plantilla Carga");

sheet.getRange("A1:E1").values = [[
  "CODIGO_ALUMNO",
  "CURSO",
  "NOTA",
  "CLASES_PROGRAMADAS",
  "CLASES_ASISTIDAS"
]];

sheet.getRange("A2:E7").values = [
  ["ALU2026001", "MATEMATICA", 14.5, 20, 18],
  ["ALU2026002", "MATEMATICA", 11, 20, 16],
  ["ALU2026003", "COMUNICACION", 13, 20, 17],
  ["ALU2026004", "COMUNICACION", 16, 20, 19],
  ["ALU2026005", "MATEMATICA", 10.5, 20, 15],
  ["ALU2026001", "COMUNICACION", 15, 20, 18]
];

sheet.getRange("G1:J6").values = [
  ["INSTRUCCIONES", null, null, null],
  ["1", "Use solo estas columnas", null, null],
  ["2", "No cambie los nombres de la cabecera", null, null],
  ["3", "NOTA debe estar entre 0 y 20", null, null],
  ["4", "CLASES_ASISTIDAS no puede ser mayor a CLASES_PROGRAMADAS", null, null],
  ["5", "Guarde el archivo como .xlsx", null, null]
];

sheet.getRange("A1:E1").format = {
  fill: "#1D4ED8",
  font: { bold: true, color: "#FFFFFF" }
};

sheet.getRange("G1:J1").merge();
sheet.getRange("G1:J1").format = {
  fill: "#7C3AED",
  font: { bold: true, color: "#FFFFFF" }
};

sheet.getRange("A2:E7").format = {
  borders: {
    top: { style: "Continuous", color: "#D1D5DB" },
    bottom: { style: "Continuous", color: "#D1D5DB" },
    left: { style: "Continuous", color: "#D1D5DB" },
    right: { style: "Continuous", color: "#D1D5DB" }
  }
};

sheet.getRange("A:E").format.autofitColumns();
sheet.getRange("G:J").format.autofitColumns();
sheet.freezePanes.freezeRows(1);

const output = await SpreadsheetFile.exportXlsx(workbook);
const outputPath = path.join(outputDir, "plantilla_carga_notas_rendimiento_academico.xlsx");
await output.save(outputPath);

console.log(outputPath);
