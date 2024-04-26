import * as express from "express";

const router = express.Router();

// 메인 페이지
router.get(
  "/",
  async (
    req: express.Request,
    res: express.Response,
    next: express.NextFunction
  ) => {
    console.log("로그인 페이지!");
    res.render("index");
  }
);

export default router;
