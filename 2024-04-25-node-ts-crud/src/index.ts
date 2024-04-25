import express, { Request, Response } from "express";
import taskRoutes from "./routes/task";

// https://dev.to/wizdomtek/typescript-express-building-robust-apis-with-nodejs-1fln

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use("/", taskRoutes);

app.get("/", (req: Request, res: Response) => {
  res.send("Hello, TypeScript Express!");
});

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
