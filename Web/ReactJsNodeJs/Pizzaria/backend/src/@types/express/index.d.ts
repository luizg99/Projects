import * as express from 'express';

declare global {
  namespace Express {
    interface Request {
      file?: Express.Multer.File; // Adiciona suporte a req.file
      files?: Express.Multer.File[]; // Se você estiver lidando com múltiplos arquivos
      user_id?: string; // Adiciona a propriedade user_id
    }
  }
}

declare namespace Express{
  export interface Request{
    user_id: string;
  }
}