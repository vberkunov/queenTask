package com.application.demo.service;


import com.application.demo.entity.QueenFile;
import com.application.demo.entity.UploadFileResponse;
import com.application.demo.repository.DataAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.mock.web.MockMultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class QueenStorageServiceImpl {
    @Autowired
    private DataAccess dataAccess;
    private static final  int N = 8;
    private   int[][] chessBoard;
    private final Logger logger = LoggerFactory.getLogger(QueenStorageServiceImpl.class.getSimpleName());
    private List<UploadFileResponse> list;



    public UploadFileResponse store(MultipartFile file) {
        String status = "";
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            byte [] byteArr=file.getBytes();
            QueenFile queenFile1 = new QueenFile(fileName, file.getContentType(), byteArr);
            long id = 0;
            try {
               id = dataAccess.save(queenFile1);
                System.out.println(id);
            }catch (Exception e){
                logger.info(e.getMessage());
            }
            status = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/queen/")
                    .path(String.valueOf(id))
                    .toUriString();
            return new UploadFileResponse(id, status);
        } catch (MaxUploadSizeExceededException e) {
            status = "Failed. File is too large";
            return new UploadFileResponse(0,status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UploadFileResponse(0,status);
    }

    public QueenFile getFile(long id) {
        return dataAccess.getFileById(id);
    }

    public List<UploadFileResponse> generate() {
        chessBoard = new int[N][N];
        list = new ArrayList<>();
        solveProblem(0, chessBoard);
        return list;
    }
    public  void solveProblem(int col, int[][] chessBoard){

        if (sumOfQueens(chessBoard) == N) {
            UploadFileResponse response =store(writeMatrix("solution"+String.valueOf(col)));
            list.add(response);
            return ;
        }


        for (int i=0; i<N; i++) {

            if (chessBoard[i][col] == 0) {
                if (checkPoint( i, col)) {
                    chessBoard[i][col] = 1;
                    solveProblem(col+1, chessBoard);
                }

                chessBoard[i][col] = 0;
            }

        }
    }

    private   boolean checkPoint(int y,int x) {

        for (int i = 0; i < N; i++) {
            if (chessBoard[y][i] == 1) {
                return false;
            }
        }
        for (int i = 0; i < N; i++) {
            if (chessBoard[i][x] == 1) {
                return false;
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (chessBoard[i][j] == 1) {
                    if (Math.abs(i - y) == Math.abs(j - x)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private   long sumOfQueens(int[][]arr){
        return Stream.of(arr).map(i-> Arrays.stream(i).filter(j->j==1).count()).reduce(Long::sum).get();

    }
    public MultipartFile writeMatrix(String filename) {
        MultipartFile multipartFile = null;
        try {
            File simpleFile = new File(filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(simpleFile));

            for (int i = 0; i < chessBoard.length; i++) {
                for (int j = 0; j < chessBoard[i].length; j++) {
                    if(j == chessBoard[i].length - 1){
                        bw.write(chessBoard[i][j]);
                    } else{
                        bw.write(chessBoard[i][j] + ",");
                    }
                }
                bw.newLine();
            }
            bw.flush();
             multipartFile = new MockMultipartFile("file",filename,"text/plain",new FileInputStream(simpleFile));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return multipartFile;
    }
}
