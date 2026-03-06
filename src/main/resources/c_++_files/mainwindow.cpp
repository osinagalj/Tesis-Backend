#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent), ui(new Ui::MainWindow){
  ui->setupUi(this);

  path =  "C:\\Users\\sebas\\Desktop\\Software\\Datos\\";
}

MainWindow::~MainWindow(){
    delete ui;
}

// Function returns the rank vector of the set of observations
void MainWindow::rankify(double *array, double *&rank, int size){

  for(int i = 0; i < size; i++){
    int r = 1, s = 1;

    // Count no of smaller elements in 0 to i-1
    for(int j = 0; j < i; j++) {
      if (array[j] < array[i])
        r++;
      if (array[j] == array[i])
        s++;
    }

    // Count no of smaller elements in i+1 to N-1
    for (int j = i+1; j < size; j++) {
      if (array[j] < array[i] )
        r++;
      if (array[j] == array[i] )
        s++;
    }

    // Use Fractional Rank formula fractional_rank = r + (n-1)/2
    rank[i] = r + (s-1) * 0.5;
  }
}



double MainWindow::getPearsonCoefficient(double **im1, double **im2, int H, int W){

  double size = H*W;

  double *X = new double[int(size)];
  double *Y = new double[int(size)];

  int pos = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      X[pos] = im1[row][col];
      Y[pos] = im2[row][col];
      pos++;
    }
  }

  // Get ranks of vector X
  double *rank_x = new double[int(size)];
  rankify(X, rank_x, int(size));
  // Get ranks of vector y
  double *rank_y = new double[int(size)];
  rankify(Y, rank_y, int(size));

  double sum_X = 0, sum_Y = 0, sum_XY = 0, squareSum_X = 0, squareSum_Y = 0;
  for (int i = 0; i < size; i++){
    // sum of elements of array X.
    sum_X = sum_X + rank_x[i];
    // sum of elements of array Y.
    sum_Y = sum_Y + rank_y[i];
    // sum of X[i] * Y[i].
    sum_XY = sum_XY + rank_x[i] * rank_y[i];
    // sum of square of array elements.
    squareSum_X = squareSum_X + rank_x[i] * rank_x[i];
    squareSum_Y = squareSum_Y + rank_y[i] * rank_y[i];
  }

  // use formula for calculating correlation coefficient.
  double corr = (size*sum_XY - sum_X*sum_Y) / (sqrt((size*squareSum_X - sum_X*sum_X)*(size*squareSum_Y - sum_Y*sum_Y)));

  return corr;
}

CapturedImage *MainWindow::convert24to8bits(IplImage *image){

  CapturedImage *item = new CapturedImage(image->width,image->height,8,1);

  CvScalar s;
  for(int row = 0; row < item->getHeight(); row++){
    for(int col = 0; col < item->getWidth(); col++){
      s= cvGet2D(image,row,col);
      int value = (int)round((s.val[0] + s.val[1] + s.val[2])/3);
      item->setValueImage(row, col, value);
    }
  }

  return  item;
}

void MainWindow::show_multiple_histograms(){

  std::string path_image = path + "Resultados\\01\\ROI\\";
  std::string path_method = "LeeR";
  std::string path_histogram = path + "Resultados\\01\\ROI\\Histograms\\";

  std::string path_filter;
  CapturedImage *image = new CapturedImage();

  int histSize[] = {256};
  float lranges[] = {0, 256};
  const float* ranges[] = {lranges};
  int channels[] = {0};
  double max_frecuency=0;
  QVector<QCPGraphData> data(256);

  for (int dist = 1; dist <=10; dist++){

    stringstream dist_str;
    dist_str<<(dist);

    for (int inx_roi = 0; inx_roi <= 3; inx_roi++){

      stringstream temp_roi;
      temp_roi<<(inx_roi);

      std::string path_filter_ROI = path_image + "ROI_" + temp_roi.str() +"_"+path_method+"_r_"+ dist_str.str()+".jpg";

      image->loadCaptureImageGrayscaleFromFile(path_filter_ROI.c_str());

      cv::Mat1b const im = cvarrToMat(image->getImage());
      cv::Mat hist;
      cv::calcHist(&im, 1, channels, cv::Mat(), hist, 1, histSize, ranges, true, false);

      for(int b = 0; b < 256; b++)
        data[b].value = 0;

       for(int b = 0; b < 256; b++) {

         data[b].key = b;
         data[b].value = ((double(hist.at<float>(b)))/(image->getHeight() * image->getWidth()))*100;

         if (b == 0)
           max_frecuency = data[b].value;
         else if (max_frecuency < data[b].value)
           max_frecuency = data[b].value;
       }

       QColor color(0, 0, 0);
       ui->Widget->clearGraphs();
       ui->Widget->legend->setFont(QFont("Helvetica",12));
       ui->Widget->xAxis->setLabel("Pixel Value");
       ui->Widget->yAxis->setLabel("Frecuency (%)");
       ui->Widget->xAxis->setRange(0, 260);
       ui->Widget->yAxis->setRange(0, max_frecuency+max_frecuency*0.01);
       ui->Widget->addGraph();
       ui->Widget->graph()->setLineStyle(QCPGraph::lsLine);
       ui->Widget->graph()->setPen(QPen(color.lighter(200)));
       ui->Widget->graph()->setBrush(QBrush(color));
       ui->Widget->graph()->data()->set(data);
       ui->Widget->replot();
       std::string path_histogram_save = path_histogram+ "H_ROI_" + temp_roi.str() +"_"+path_method+"_r_"+ dist_str.str()+".jpg";
       ui->Widget->saveJpg(path_histogram_save.c_str(),  0, 0, 1.0, -1  );
     }
  }
}


void MainWindow::show_histogram(cv::Mat1b const& image){

  int histSize[] = {256};
  float lranges[] = {0, 256};
  const float* ranges[] = {lranges};
  cv::Mat hist;
  int channels[] = {0};

  cv::calcHist(&image, 1, channels, cv::Mat(), hist, 1, histSize, ranges, true, false);

  double max_frecuency = 0;

  QVector<QCPGraphData> data(256);

  for(int b = 0; b < 256; b++) {

    data[b].key = b;
    data[b].value = ((double(hist.at<float>(b)))/(image.rows * image.cols))*100;

    if (b == 0)
      max_frecuency = data[b].value;
    else if (max_frecuency < data[b].value)
      max_frecuency = data[b].value;
  }

  QColor color(0, 0, 0);
  ui->Widget->legend->setFont(QFont("Helvetica",12));
  ui->Widget->xAxis->setLabel("Pixel Value");
  ui->Widget->yAxis->setLabel("Frecuency (%)");
  ui->Widget->xAxis->setRange(0, 260);
  ui->Widget->yAxis->setRange(0, max_frecuency+max_frecuency*0.01);
  ui->Widget->addGraph();
  ui->Widget->graph()->setLineStyle(QCPGraph::lsLine);
  ui->Widget->graph()->setPen(QPen(color.lighter(200)));
  ui->Widget->graph()->setBrush(QBrush(color));
  ui->Widget->graph()->data()->set(data);
  ui->Widget->replot();
 }

void MainWindow::TestHistogramImage(){

  std::string path_I1 = path + "Resultados\\02\\LeeR_6.jpg";
  CapturedImage *im1 = new CapturedImage();
  im1->loadCaptureImageGrayscaleFromFile(path_I1.c_str());

  show_histogram(cvarrToMat(im1->getImage()));
}

void MainWindow::imageToDoubleArray(CapturedImage *image, double **array){

  int H = image->getHeight();
  int W = image->getWidth();

  for(int row = 0; row < H; row++){
    for(int col = 0; col < W; col++){
      array[row][col] =image->getValueImage(row, col);
    }
  }
}


void MainWindow::TestCorrelationImages(){

  ofstream fs("LeeR10.txt");

  //int radius = 2;

  std::string path_I1 = path + "Resultados\\02\\LeeR_10.jpg";
  CapturedImage *im1 = new CapturedImage();
  im1->loadCaptureImageGrayscaleFromFile(path_I1.c_str());

  int H = im1->getHeight();
  int W = im1->getWidth();

  for (int row = 0; row < H;  row++){
    for (int col = 0; col < W;  col++){
      fs << im1->getValueImage(row, col) << "\t";
    }
    fs << endl;
  }

  fs.close();

  /*
  std::string path_I2 = path + "Resultados\\02\\LeeR_7.jpg";
  CapturedImage *im2 = new CapturedImage();
  im2->loadCaptureImageGrayscaleFromFile(path_I2.c_str());

  int H = im1->getHeight();
  int W = im1->getWidth();

  double **Y = new double *[H];
  for(int i = 0; i < H; i++)
    Y[i] = new double[W];

  int D = 2*radius+1;
  double **im1_d = new double *[D];
  double **im2_d = new double *[D];
  for(int i = 0; i < D; i++){
    im1_d[i] = new double[D];
    im2_d[i] = new double[D];
  }

  CapturedImage *resultado = new CapturedImage(W,H,im1->getDepth(),im1->getChannels());


  printf("\n ");
  printf("\n ");
  for (int row = 0; row < H;  row++){
    for (int col = 0; col < W;  col++){

      for(int rw = 0; rw < D; rw++){
        for(int cw = 0; cw < D; cw++){
          im1_d[rw][cw] = 0.0;
          im2_d[rw][cw] = 0.0;
        }
      }
      int r=0;
      for (int i = -radius; i <= radius; i++){
        int c=0;
        for (int j = -radius; j <= radius; j++){
          if((row+i >= 0)&&((col+j >= 0)&&(row+i < H)&&(col+j < W))){
              im1_d[r][c] = im1->getValueImage(row+i, col+j);
              im2_d[r][c] = im2->getValueImage(row+i, col+j);
          }
          c++;
        }
        r++;
      }

      Y[row][col] = getPearsonCoefficient(im1_d, im2_d, D, D);
      fs << Y[row][col] << "\t";


      //if (isnan(Y[row][col]))
      //  fs << 0 << "\t";
      //else
      //  fs << Y[row][col] << "\t";

      // X' = a + ((X-Xmin)*(b-a) / (Xmax - Xmin)) a =0 b = 255 Xmin = -1 Xmax = 1 X = -1..1
     // double a = 0, b =255, Xmin = -1, Xmax =1;
     // int value = int(a+(((Y[row][col]-Xmin)*(b-a))/(Xmax-Xmin)));
      //if (Y[row][col] > 0 &&  Y[row][col] < 0.4)
      //  resultado->setValueImage(row,col,255);
      //else
      //  resultado->setValueImage(row,col,0);

    }
    fs << endl;
  }

  resultado->saveCaptureImage("resultado");
  fs.close();
*/
}

void MainWindow::UnsharpMask(CapturedImage *Original, CapturedImage *&dst){

  IplImage *im1 = Original->getImage();

  Mat i1 = cvarrToMat(im1);

  // sharpen image using "unsharp mask" algorithm
  Mat blurred; double sigma = 1, threshold = 5, amount = 1;
  GaussianBlur(i1, blurred, Size(5,5), sigma, sigma);


  Mat lowContrastMask = abs(i1 - blurred) < threshold;
  Mat sharpened = i1*(1+amount) + blurred*(-amount);

  i1.copyTo(sharpened, lowContrastMask);

  dst = new CapturedImage(Original->getWidth(), Original->getHeight(),Original->getDepth(), Original->getChannels());
  for (int row=0; row< Original->getHeight(); row++){
    for (int col=0; col< Original->getWidth(); col++){
      dst->setValueImage(row, col, sharpened.at<uchar>(row,col));
    }
  }
}
void MainWindow::on_LeeButton_clicked(){
  ui->LeeButton->setEnabled(true);

  std::string path_image_originales = path + "Originales\\37.png";
  CapturedImage *Original = new CapturedImage();
  Original->loadCaptureImageGrayscaleFromFile(path_image_originales.c_str());

  //-2 y -1 x
  /*int pixels_art[35][2] = {{48,120},{49,120},{50,120},{51,120},{52,120},{52,119},{53,119},{54,119},{54,120},{54,121},{54,122},
                           {55,119},{55,120},{55,121},{55,122},
                           {56,119},{56,120},{56,121},{56,122},
                           {57,119},{57,120},{57,121},{57,122},
                           {58,119},{58,120},{58,121},{58,122},
                           {59,119},{59,120},{59,121},{59,122},
                           {60,119},{60,120},{60,121},{60,122}
                     };

  int pixels_no_art[19][2] = {{404,177},{405,177},{406,177},{407,177},{408,177},{409,177},{410,177},{411,177},{412,177},
                              {408,178},{409,178},{410,178},{411,178},{412,178},{413,178},{414,178},{415,178},{416,178},{417,178}};



  for (int p = 0; p < 19; p++){
    printf("\n\n row \t col \t xi \t radius \t pixels \t me \t Q1 \t Q3 \t IQR_W \t IQR_I \t IQR_W/IQR_I \t Residual \t T2*(xi-me) \t Y_real \t Y \t xi < Q1 o xi >Q3 \t |xi-me|/me \t");

    for (int dist = 1; dist <= 10; dist++)
      CapturedImage *leeR = leeRobustFilter(Original, dist,pixels_no_art[p][0], pixels_no_art[p][1]);
  }*/

 /* CapturedImage *dst;
  UnsharpMask(Original, dst);

  std::string path_image_resultados = path + "Resultados\\Test\\";

  std::string path_UM = path_image_resultados + "UM";

  dst->saveCaptureImage(path_UM.c_str());
*/
/*
  int dist = 6;
  std::string path_image_resultados = path + "Resultados\\Test\\";

  stringstream square_str;
  square_str<<(dist);
  std::string path_square = path_image_resultados + "Square_" + square_str.str();

  //int inx_roi = 9;

  CapturedImage *leeR = leeRobustFilter(Original, dist);

  stringstream leeR_str;
  leeR_str<<(dist);
  std::string path_leeR = path_image_resultados + "IQRw_IQRi_" + leeR_str.str();
  leeR->saveCaptureImage(path_leeR.c_str());
*/


  /*
  double min, max;
  for (double IQRw =0; IQRw<= 255; IQRw++){
    for (double IQRi =1; IQRi<= 255; IQRi++){

      double z = IQRw/(IQRi+IQRw);
      //printf("%f \n", z);

      if (IQRw == 0.0 && IQRi == 0.0){
        min = 0;
        max = 0;
      }
      else{
        if (z < min)
          min = z;
        if (z > max)
          max = z;
      }
    }
  }
  printf("min: %f max: %f ", min, max);
*/

 /* printf("[z,\tIQRw,\tIQRI,\tx,\tme] \n");
  for (double IQRw =0; IQRw<= 255; IQRw++){
    for (double IQRi =52; IQRi<= 52; IQRi++){
      for (double x =0; x<= 255; x++){
        for (double me =0; me<= 255; me++){
           double z = me + (IQRw/(IQRi+IQRw))*(x-me);
           if (z<0 || z> 255)
             printf("[%f,\t%f,\t%f,\t%f,\t%f] \n", z, IQRw, IQRi, x, me);
        }
      }
    }
  }*/



//  CapturedImage *lR = new CapturedImage(20, 20, Original->getDepth(), Original->getChannels());
//  leeR->cropImage(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h, lR);
//  lR->saveCaptureImage(path_leeR.c_str());


  //std::string path_image_originales = path + "Originales\\19_original.jpg";
  //IplImage *o = cvLoadImage(path_image_originales.c_str(),1);
  //CapturedImage *Original = convert24to8bits(o);
  //Original->saveCaptureImage("C:\\Users\\sebas\\Desktop\\Software\\cruda");
  //show_histogram(cvarrToMat(Original->getImage()));
  //  show_multiple_histograms();


/* // Pearson Coefficient
  //std::string path_image_1 = path + "Resultados\\20\\sint_001\\ROI\\ROI_5_ORIGINAL.jpg";
  std::string path_image_1 = path + "Originales\\20.jpg";
  CapturedImage *im1 = new CapturedImage();

  im1->loadCaptureImageGrayscaleFromFile(path_image_1.c_str());

  //std::string path_image_2 = path + "Resultados\\20\\sint_001\\ROI\\ROI_5_Lee_r_10.jpg";
  std::string path_image_2 = path + "Resultados\\20\\sint_025\\LeeR_10.jpg";
  CapturedImage *im2 = new CapturedImage();
  im2->loadCaptureImageGrayscaleFromFile(path_image_2.c_str());

  printf("\n%f \n\n", getPearsonCoefficient(im1, im2));
*/
/*
  // Process
  std::string path_image_resultados = path + "Resultados\\37\\";

  CapturedImage *mf, *lee, *leeR, *newLeeR;
  for (int dist = 1; dist <=30; dist++){

     mf = medianFilterHuang(Original, 0.5, dist);
     lee = leeFilter(Original, dist);
     leeR = leeRobustFilter(Original, dist);
     newLeeR = newLeeRobustFilter(Original, dist);

     stringstream temp_str;
     temp_str<<(dist);
     std::string path_mf = path_image_resultados  + "MF_" + temp_str.str();
     std::string path_lee = path_image_resultados + "Lee_" + temp_str.str();
     std::string path_leeR = path_image_resultados + "LeeR_" + temp_str.str();
     std::string path_new_lee = path_image_resultados + "NewLeeR_" + temp_str.str();

     mf->saveCaptureImage(path_mf.c_str());
     lee->saveCaptureImage(path_lee.c_str());
     leeR->saveCaptureImage(path_leeR.c_str());
     newLeeR->saveCaptureImage(path_new_lee.c_str());
   }
*/

  // Algunas Metricas
   std::string path_image_resultados = path + "Resultados\\37\\";
   std::string path_mf, path_lee, path_leeR;
   CapturedImage *ImMF = new CapturedImage();
   CapturedImage *ImLee = new CapturedImage();
   CapturedImage *ImLeeR = new CapturedImage();

   printf("\n \t PSNR"
          "\t \t \t SSIM"
          "\t \t \t SRS"
          "\t \t \t MACANA"
          "\t \t \t CONTRAST");
   printf("\nRadius \t MF \t Lee \t LeeR"
                   "\t MF \t Lee \t LeeR"
                   "\t MF \t Lee \t LeeR"
                   "\t MF \t Lee \t LeeR"
                   "\t MF \t Lee \t LeeR");

   for (int dist = 1; dist <=30; dist++){

     stringstream dist_str;
     dist_str<<(dist);
     path_mf = path_image_resultados   + "MF_" + dist_str.str()   +".png";
     path_lee = path_image_resultados  + "Lee_" + dist_str.str()  +".png";
     path_leeR = path_image_resultados + "LeeR_" + dist_str.str() +".png";

     ImMF->loadCaptureImageGrayscaleFromFile(path_mf.c_str());
     ImLee->loadCaptureImageGrayscaleFromFile(path_lee.c_str());
     ImLeeR->loadCaptureImageGrayscaleFromFile(path_leeR.c_str());

     Scalar m_luminance_IM, m_contrast_IM, m_structure_IM;
     getMSSIM(Original, ImMF, m_luminance_IM, m_contrast_IM, m_structure_IM);
     Scalar m_luminance_Lee, m_contrast_Lee, m_structure_Lee;
     getMSSIM(Original, ImLee, m_luminance_Lee, m_contrast_Lee, m_structure_Lee);
     Scalar m_luminance_LeeR, m_contrast_LeeR, m_structure_LeeR;
     getMSSIM(Original, ImLeeR, m_luminance_LeeR, m_contrast_LeeR, m_structure_LeeR);

     printf("\n %d \t %f \t %f \t %f"
                  "\t %f \t %f \t %f"
                  "\t %f \t %f \t %f"
                  "\t %f \t %f \t %f"
                  "\t %f \t %f \t %f",

            dist,

            getPSNR(Original, ImMF),
            getPSNR(Original, ImLee),
            getPSNR(Original, ImLeeR),

            getMSSIM(Original, ImMF).val[0] ,
            getMSSIM(Original, ImLee).val[0] ,
            getMSSIM(Original, ImLeeR).val[0] ,

            speackle_reduction_score(Original, ImMF).val[0],
            speackle_reduction_score(Original, ImLee).val[0],
            speackle_reduction_score(Original, ImLeeR).val[0],

            macana_metric(Original, ImMF),
            macana_metric(Original, ImLee),
            macana_metric(Original, ImLeeR),

            m_contrast_IM.val[0],
            m_contrast_Lee.val[0],
            m_contrast_LeeR.val[0]
     );
   }



/*
 // Todas Metricas
  std::string path_image_resultados = path + "Resultados\\20\\sint_25_imj\\";
  std::string path_mf, path_lee, path_leeR, path_newleeR;
  CapturedImage *ImMF = new CapturedImage();
  CapturedImage *ImLee = new CapturedImage();
  CapturedImage *ImLeeR = new CapturedImage();
  CapturedImage *ImNewLeeR = new CapturedImage();

  printf("\n \t PSNR"
         "\t \t \t \t ENL"
         "\t \t \t \t SSI"
         "\t \t \t \t SMPI"
         "\t \t \t \t SSIM"
         "\t \t \t \t SRS"
         "\t \t \t \t MACANA"
         "\t \t \t \t ENTROPY"
         "\t \t \t \t LUMINANCE"
         "\t \t \t \t CONTRAST"
         "\t \t \t \t STRUCTURE");
  printf("\nRadius \t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR"
                  "\t MF \t Lee \t LeeR \t NewLeeR");

  for (int dist = 1; dist <=10; dist++){

    stringstream dist_str;
    dist_str<<(dist);
    path_mf = path_image_resultados   + "MF_" + dist_str.str()   +".jpg";
    path_lee = path_image_resultados  + "Lee_" + dist_str.str()  +".jpg";
    path_leeR = path_image_resultados + "LeeR_" + dist_str.str() +".jpg";
    path_newleeR = path_image_resultados + "NewLeeR_" + dist_str.str() +".jpg";

    ImMF->loadCaptureImageGrayscaleFromFile(path_mf.c_str());
    ImLee->loadCaptureImageGrayscaleFromFile(path_lee.c_str());
    ImLeeR->loadCaptureImageGrayscaleFromFile(path_leeR.c_str());
    ImNewLeeR->loadCaptureImageGrayscaleFromFile(path_newleeR.c_str());

    Scalar m_luminance_IM, m_contrast_IM, m_structure_IM;
    getMSSIM(Original, ImMF, m_luminance_IM, m_contrast_IM, m_structure_IM);
    Scalar m_luminance_Lee, m_contrast_Lee, m_structure_Lee;
    getMSSIM(Original, ImLee, m_luminance_Lee, m_contrast_Lee, m_structure_Lee);
    Scalar m_luminance_LeeR, m_contrast_LeeR, m_structure_LeeR;
    getMSSIM(Original, ImLeeR, m_luminance_LeeR, m_contrast_LeeR, m_structure_LeeR);
    Scalar m_luminance_NewLeeR, m_contrast_NewLeeR, m_structure_NewLeeR;
    getMSSIM(Original, ImNewLeeR, m_luminance_NewLeeR, m_contrast_NewLeeR, m_structure_NewLeeR);

    printf("\n %d \t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f"
                 "\t %f \t %f \t %f \t %f",

           dist,

           getPSNR(Original, ImMF),
           getPSNR(Original, ImLee),
           getPSNR(Original, ImLeeR),
           getPSNR(Original, ImNewLeeR),

           getENL(ImMF),
           getENL(ImLee),
           getENL(ImLeeR),
           getENL(ImNewLeeR),

           getSSI(Original, ImMF),
           getSSI(Original, ImLee),
           getSSI(Original, ImLeeR),
           getSSI(Original, ImNewLeeR),

           getSMPI(Original, ImMF),
           getSMPI(Original, ImLee),
           getSMPI(Original, ImLeeR),
           getSMPI(Original, ImNewLeeR),

           getMSSIM(Original, ImMF).val[0] ,
           getMSSIM(Original, ImLee).val[0] ,
           getMSSIM(Original, ImLeeR).val[0] ,
           getMSSIM(Original, ImNewLeeR).val[0] ,

           speackle_reduction_score(Original, ImMF).val[0],
           speackle_reduction_score(Original, ImLee).val[0],
           speackle_reduction_score(Original, ImLeeR).val[0],
           speackle_reduction_score(Original, ImNewLeeR).val[0],

           macana_metric(Original, ImMF),
           macana_metric(Original, ImLee),
           macana_metric(Original, ImLeeR),
           macana_metric(Original, ImNewLeeR),

           entropy_metric(ImMF),
           entropy_metric(ImLee),
           entropy_metric(ImLeeR),
           entropy_metric(ImNewLeeR),

           m_luminance_IM.val[0],
           m_luminance_Lee.val[0],
           m_luminance_LeeR.val[0],
           m_luminance_NewLeeR.val[0],

           m_contrast_IM.val[0],
           m_contrast_Lee.val[0],
           m_contrast_LeeR.val[0],
           m_contrast_NewLeeR.val[0],

           m_structure_IM.val[0],
           m_structure_Lee.val[0],
           m_structure_LeeR.val[0],
           m_structure_NewLeeR.val[0]

    );
  }
*/
/*
 // Original con Original metricas
  std::string path_image_2 = path + "Originales\\31.png";
  CapturedImage *im2 = new CapturedImage();
  im2->loadCaptureImageGrayscaleFromFile(path_image_2.c_str());

  std::string path_image_3 = path + "Originales\\31_ruido.png";
  CapturedImage *im3 = new CapturedImage();
  im3->loadCaptureImageGrayscaleFromFile(path_image_3.c_str());

  printf("\n \t PSNR"
         "\t \t \t SSIM"
         "\t \t \t SRS"
         "\t \t \t MACANA"
         "\t \t \t CONTRAST");
  printf("\nRadius \t OO \t OR"
                  "\t OO \t OR"
                  "\t OO \t OR"
                  "\t OO \t OR"
                  "\t OO \t OR");

 Scalar m_luminance_OO, m_contrast_OO, m_structure_OO;
 getMSSIM(Original, im2, m_luminance_OO, m_contrast_OO, m_structure_OO);
 Scalar m_luminance_OR, m_contrast_OR, m_structure_OR;
 getMSSIM(Original, im3, m_luminance_OR, m_contrast_OR, m_structure_OR);

 printf("\n %d \t %f"
              "\t %f"
              "\t %f"
              "\t %f"
              "\t %f",

           0,

           getPSNR(Original, im2),

           getMSSIM(Original, im2).val[0] ,

           speackle_reduction_score(Original, im2).val[0],

           macana_metric(Original, im2),

           m_contrast_OO.val[0]
    );

   printf("\n %d \t %f"
              "\t %f"
              "\t %f"
              "\t %f"
              "\t %f",

           0,

           getPSNR(Original, im3),

           getMSSIM(Original, im3).val[0] ,

           speackle_reduction_score(Original, im3).val[0],

           macana_metric(Original, im3),

           m_contrast_OR.val[0]
    );

*/

  // Percentile
  /*std::string path_image_originales = path + "Originales\\02.jpg";
  CapturedImage *Original = new CapturedImage();
  Original->loadCaptureImageGrayscaleFromFile(path_image_originales.c_str());
 // Original->loadCaptureImageColorFromFile(path_image_originales.c_str());

  int radius = 3;
  int perInf = 25;
  int perSup = 95;

  CapturedImage *leeRP = leeRobustPercentileVersion(Original, radius, perInf, perSup);

  Scalar m_luminance_leeRP, m_contrast_leeRP, m_structure_leeRP;
  getMSSIM(Original, leeRP, m_luminance_leeRP, m_contrast_leeRP, m_structure_leeRP);

  printf("\nRadius: %d PerInf: %d PerSup: %d PSNR: %f SSIM: %f SRS: %f MACANA: %f CONTRAST: %f\n\n",
                                                                  radius,
                                                                  perInf,
                                                                  perSup,
                                                                  getPSNR(Original, leeRP),
                                                                  getMSSIM(Original, leeRP).val[0],
                                                                  speackle_reduction_score(Original, leeRP).val[0],
                                                                  macana_metric(Original, leeRP),
                                                                  m_contrast_leeRP.val[0]);

  std::string path_image_resultados = path + "Resultados\\Percentile\\02\\";

  stringstream temp_radius;
  temp_radius<<(radius);

  stringstream temp_perInf;
  temp_perInf<<(perInf);

  stringstream temp_perSup;
  temp_perSup<<(perSup);

  std::string path_leeRP = path_image_resultados + "LeeRP_" + temp_radius.str()+ "_" +temp_perInf.str() + "_" + temp_perSup.str();

  leeRP->saveCaptureImage(path_leeRP.c_str());
*/
/*
  std::string path_image_originales = path + "Originales\\20.jpg";
  CapturedImage *Original = new CapturedImage();
  Original->loadCaptureImageColorFromFile(path_image_originales.c_str());
  Original->loadCaptureImageGrayscaleFromFile(path_image_originales.c_str());

  std::string path_image_resultados_ROI = path + "Resultados\\20\\sint_001\\ROI\\";
  std::string path_image_resultados = path + "Resultados\\20\\sint_001\\";

  std::string path_mf, path_lee, path_leeR;

  CapturedImage *ImMF = new CapturedImage();
  CapturedImage *ImLee = new CapturedImage();
  CapturedImage *ImLeeR = new CapturedImage();
  CapturedImage *mf_roi, *lee_roi, *leeR_roi;//, *original_roi;


 // for (int inx_roi = 4; inx_roi <= 7; inx_roi++){
  //  Original->cropImage(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h, original_roi);
  //  stringstream temp_o_roi;
  //  temp_o_roi<<(inx_roi);

  //  std::string path_original_ROI = path_image_resultados_ROI + "ROI_" + temp_o_roi.str() +"_ORIGINAL";
  //  original_roi->saveCaptureImage(path_original_ROI.c_str());
  //  Original->drawSquare(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h);
  //}

  //std::string path_ROI = path_image_resultados_ROI + "ROI";
 // Original->saveCaptureImage(path_ROI.c_str());

  for (int dist = 1; dist <= 10; dist++){

    stringstream dist_str;
    dist_str<<(dist);

    path_mf = path_image_resultados + "MF_" + dist_str.str() +".jpg";
    path_lee = path_image_resultados + "Lee_" + dist_str.str() +".jpg";
    path_leeR = path_image_resultados + "LeeR_" + dist_str.str() +".jpg";

    ImMF->loadCaptureImageGrayscaleFromFile(path_mf.c_str());
    ImLee->loadCaptureImageGrayscaleFromFile(path_lee.c_str());
    ImLeeR->loadCaptureImageGrayscaleFromFile(path_leeR.c_str());

    for (int inx_roi = 4; inx_roi <= 7; inx_roi++){

      stringstream temp_roi;
      temp_roi<<(inx_roi);

      ImMF->cropImage(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h, mf_roi);
      ImLee->cropImage(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h, lee_roi);
      ImLeeR->cropImage(roi[inx_roi].r, roi[inx_roi].c, roi[inx_roi].w, roi[inx_roi].h, leeR_roi);

      std::string path_mf_ROI = path_image_resultados_ROI + "ROI_" + temp_roi.str() +"_MF_r_"+ dist_str.str();
      std::string path_lee_ROI = path_image_resultados_ROI + "ROI_" + temp_roi.str() +"_Lee_r_"+ dist_str.str();
      std::string path_leeR_ROI = path_image_resultados_ROI + "ROI_"+ temp_roi.str() +"_LeeR_r_"+ dist_str.str();

      mf_roi->saveCaptureImage(path_mf_ROI.c_str());
      lee_roi->saveCaptureImage(path_lee_ROI.c_str());
      leeR_roi->saveCaptureImage(path_leeR_ROI.c_str());
   }
  }*/
}

/***********************************************************************************************
 *
 *                                                PROCESS
 *
 ***********************************************************************************************/

// Lee Robust Percentile Version
CapturedImage* MainWindow::leeRobustPercentileVersion(CapturedImage *item, int radius, int perInf, int perSup){

  int H = item->getHeight();
  int W = item->getWidth();

  CapturedImage *aux = new CapturedImage(W, H, item->getDepth(),item->getChannels());

  int b = 256;
  int v;

  int nc = H*W;

  // Create global histogram
  int *global_histogram = new int [b];
  // Inicializar global histogram con 0

  for(int i = 0; i < b; i++)
    global_histogram[i] = 0;

  // Inicializar global histogram con valores de la imagen
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = item->getValueImage(row,col);
      global_histogram[v]++;
    }
  }

  double pI = double(perInf)/100;
  double pS = double(perSup)/100;
  double pm = (pI+pS)/2;

  int perInf_global = statistic_order(global_histogram, nc, b, pI);
  int perSup_global = statistic_order(global_histogram, nc, b, pS);
  double inter_Q_global = perSup_global - perInf_global;

  // Create local histogram
  int **local_histogram = new int *[H];
  for(int row = 0; row < H; row++)
    local_histogram[row] = new int[b];

  // inicializar local histogram con 0
  for (int row = 0; row < H; row++){
    for (int col = 0; col < b; col++){
      local_histogram[row][col] = 0;
    }
  }

  // inicializar local histogram con valores inicales
  int icol = 0;
  for (int row = 0; row < H; row++){
    for (int i = -radius; i <= radius; i++){
      for (int j = -radius; j <= radius; j++){
        if((row+i >= 0)&&((icol+j >= 0)&&(row+i < H)&&(icol+j < W)))
          local_histogram[row][item->getValueImage(row+i,icol+j)]++;
      }
    }
  }
  double y;
  // Algoritmo de Huang
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){

      nc = numberCells(item,row,col,radius);

      int perInf_local = statistic_order(local_histogram, row, nc, b, pI);
      int median = statistic_order(local_histogram, row, nc, b, pm);
      int perSup_local = statistic_order(local_histogram, row, nc, b, pS);
      double inter_Q_local = perSup_local - perInf_local;

       // Actualizar histograma
      for (int i = -radius; i <= radius; i++){
        // Eliminado de columna
        if((row+i >= 0)&&((col-radius >= 0)&&(row+i < H)&&(col-radius < W)))
          local_histogram[row][item->getValueImage(row+i,col-radius)]--;
        // Agregando de columna
        if((row+i >= 0)&&((col+radius+1 >= 0)&&(row+i < H)&&(col+radius+1 < W)))
          local_histogram[row][item->getValueImage(row+i,col+radius+1)]++;
      }
      y = median + inter_Q_local/inter_Q_global * (item->getValueImage(row,col)-median);

      aux->setValueImage(row,col,int(y));
    }
  }
  return aux;
}


// Median Filter Huang
CapturedImage* MainWindow::medianFilterHuang(CapturedImage *item, double k, int dist){

  CapturedImage *aux = new CapturedImage(item->getWidth(),item->getHeight(),item->getDepth(),item->getChannels());

  // Create array histogram
  int **histogram = new int *[item->getHeight()];
  for(int row = 0; row < item->getHeight(); row++)
    histogram[row] = new int[256];

  // inicializar histograma con 0
  for (int row = 0; row < item->getHeight(); row++){
    for (int col = 0; col < 256; col++){
      histogram[row][col] = 0;
    }
  }

  // inicializar histograma con valores inicales
  int icol = 0;
  for (int row = 0; row < item->getHeight(); row++){
    for (int i = -dist; i <= dist; i++){
      for (int j = -dist; j <= dist; j++){
        if((row+i >= 0)&&((icol+j >= 0)&&(row+i < item->getHeight())&&(icol+j < item->getWidth())))
          histogram[row][item->getValueImage(row+i,icol+j)]++;
      }
    }
  }

  // Algoritmo de Huang
  int nc = 0;
  for (int row = 0; row < item->getHeight(); row++){
    for (int col = 0; col < item->getWidth(); col++){

      // Calculate median
      int i = 0, suma = 0;

      nc = numberCells(item,row,col,dist);

      while ((i < 256) && (suma <= k * nc)){
        suma = suma + histogram[row][i];
        i = i+1;
      }

      // Actualizar histograma
      for (int i = -dist; i <= dist; i++){
        // Eliminado de columna
        if((row+i >= 0)&&((col-dist >= 0)&&(row+i < item->getHeight())&&(col-dist < item->getWidth())))
          histogram[row][item->getValueImage(row+i,col-dist)]--;
        // Agregando de columna
        if((row+i >= 0)&&((col+dist+1 >= 0)&&(row+i < item->getHeight())&&(col+dist+1 < item->getWidth())))
          histogram[row][item->getValueImage(row+i,col+dist+1)]++;
      }
      aux->setValueImage(row,col,i-1);
    }
  }
  return aux;
}

// Lee Filter
CapturedImage* MainWindow::leeFilter(CapturedImage *item, int radius){

  int H = item->getHeight();
  int W = item->getWidth();

  CapturedImage *aux = new CapturedImage(W, H, item->getDepth(),item->getChannels());

  int nc = 0;
  double v;
  double y;

  // Global Mean
  double global_mean = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = item->getValueImage(row,col);
      global_mean = global_mean + v;
    }
  }
  global_mean = global_mean/(H*W);

  // variance
  double global_square_sum = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = item->getValueImage(row,col);
      global_square_sum = global_square_sum + (v - global_mean)*(v - global_mean);
    }
  }
  double global_variance = global_square_sum/(H*W);

  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){

      nc = numberCells(item,row,col,radius);

      // local mean
      double local_mean = 0;
      for (int i = -radius; i <= radius; i++){
        for (int j = -radius; j <= radius; j++){
           if((row+i >= 0)&&((col+j >= 0)&&(row+i < H)&&(col+j < W))){
             v = item->getValueImage(row+i,col+j);
             local_mean = local_mean + v;            
         }
       }
      }

      local_mean = local_mean/nc;

      double local_square_sum = 0;
      for (int i = -radius; i <= radius; i++){
        for (int j = -radius; j <= radius; j++){
          if((row+i >= 0)&&((col+j >= 0)&&(row+i < H)&&(col+j < W))){
             v = item->getValueImage(row+i,col+j);
             local_square_sum = local_square_sum + (v - local_mean)*(v - local_mean);
          }
        }
      }
      double local_variance = local_square_sum/nc;

      v = item->getValueImage(row,col);
      y = local_mean + (v-local_mean) * (local_variance/(local_variance+global_variance));

      aux->setValueImage(row,col,int(y));
    }
  }


  return aux;
}

// Lee Robust Filter
CapturedImage* MainWindow::leeRobustFilter(CapturedImage *item, int radius/*, int r, int c*/){

   /* IplImage *im1 = Original->getImage();

    Mat i1 = cvarrToMat(im1);

    // sharpen image using "unsharp mask" algorithm
    Mat blurred; double sigma = 1, threshold = 5, amount = 1;
    GaussianBlur(i1, blurred, Size(), sigma, sigma);

    Mat lowContrastMask = abs(i1 - blurred) < threshold;
    Mat sharpened = i1*(1+amount) + blurred*(-amount);

    i1.copyTo(sharpened, lowContrastMask);
*/

  CapturedImage *blurred = medianFilterHuang(item,0.3,1);

  int H = item->getHeight();
  int W = item->getWidth();

  CapturedImage *aux = new CapturedImage(W, H, item->getDepth(),item->getChannels());

  int b = 256;
  int v;

  int nc = H*W;

  // Create global histogram
  int *global_histogram = new int [b];
  // Inicializar global histogram con 0
  for(int i = 0; i < b; i++)
    global_histogram[i] = 0;

  // Inicializar global histogram con valores de la imagen
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = item->getValueImage(row,col);
      global_histogram[v]++;
    }
  }

  int q1_global = statistic_order(global_histogram, nc, b, 0.25);
  int q3_global = statistic_order(global_histogram, nc, b, 0.75);
  double inter_Q_global = q3_global - q1_global;

  //printf("\nIQ_Global: %f \n", inter_Q_global);

  // Create local histogram
  int **local_histogram = new int *[H];
  for(int row = 0; row < H; row++)
    local_histogram[row] = new int[b];

  // inicializar local histogram con 0
  for (int row = 0; row < H; row++){
    for (int col = 0; col < b; col++){
      local_histogram[row][col] = 0;
    }
  }


  // inicializar local histogram con valores inicales
  int icol = 0;
  for (int row = 0; row < H; row++){
    for (int i = -radius; i <= radius; i++){
      for (int j = -radius; j <= radius; j++){
        if((row+i >= 0)&&((icol+j >= 0)&&(row+i < H)&&(icol+j < W)))
          local_histogram[row][item->getValueImage(row+i,icol+j)]++;
      }
    }
  }

  int *test = new int [b];
  for(int i = 0; i < b; i++)
    test[i] = 0;

  double y;
  // Algoritmo de Huang
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){

      nc = numberCells(item,row,col,radius);

      int q1_local = statistic_order(local_histogram, row, nc, b, 0.25);
      int median = statistic_order(local_histogram, row, nc, b, 0.5);
      int q3_local = statistic_order(local_histogram, row, nc, b, 0.75);
      double inter_Q_local = q3_local - q1_local;

       // Actualizar histograma
      for (int i = -radius; i <= radius; i++){
        // Eliminado de columna
        if((row+i >= 0)&&((col-radius >= 0)&&(row+i < H)&&(col-radius < W)))
          local_histogram[row][item->getValueImage(row+i,col-radius)]--;
        // Agregando de columna
        if((row+i >= 0)&&((col+radius+1 >= 0)&&(row+i < H)&&(col+radius+1 < W)))
          local_histogram[row][item->getValueImage(row+i,col+radius+1)]++;
      }

      y = median + inter_Q_local/inter_Q_global * (item->getValueImage(row,col)-median);

     // y = item->getValueImage(row,col) + (inter_Q_local/inter_Q_global)*(item->getValueImage(row,col)) - (inter_Q_local/inter_Q_global)*blurred->getValueImage(row,col);

    //   Mat sharpened = i1*(1+amount) + blurred*(-amount);
      aux->setValueImage(row,col,int(y));

  /*    if (row == r && col == c){
        bool b;
        if (item->getValueImage(row,col)< q1_local || item->getValueImage(row,col )> q3_local)
          b = 1;
        else
          b = 0;
        double r =item->getValueImage(row,col)-median;
        if (r <0)
          r = -r;
        r = r/median;
        printf("\n %d \t %d \t %d \t %d \t %d \t %d \t %d \t %d \t %d \t %d \t %f \t %d \t %f \t %f \t %d \t %d \t %f",
              row,
              col,
              item->getValueImage(row,col),
              radius,
              (2*radius+1)*(2*radius+1),
              median,
              q1_local,
              q3_local,
             int(inter_Q_local),
             int(inter_Q_global),
             inter_Q_local/inter_Q_global,
             item->getValueImage(row,col)-median,
             inter_Q_local/inter_Q_global * (item->getValueImage(row,col)-median),
             y,
             int(y),
             b,
             r);
      }*/

    /*  if (row==row_i_S0 && col==col_i_S0){
        printf("Pixel value: %d \n", item->getValueImage(row,col));
        printf("IQ_Local: %f \n", inter_Q_local);

        printf("Q3: %d \n", q3_local);
        printf("Q1: %d \n", q1_local);

        printf("IQ_Local/IQ_Global: %f \n", inter_Q_local/inter_Q_global);
        printf("Median: %d \n", median);
        printf("y: %f \n", y);
        printf("y: %d \n", int(y));
      }*/



    }
  } 

  return aux;
}

// New Lee Robust Filter
CapturedImage* MainWindow::newLeeRobustFilter(CapturedImage *item, int radius){

  int H = item->getHeight();
  int W = item->getWidth();

  CapturedImage *aux = new CapturedImage(W, H, item->getDepth(),item->getChannels());

  int b = 256;
  int v;

  int nc = H*W;

  // Create global histogram
  int *global_histogram = new int [b];
  // Inicializar global histogram con 0

  for(int i = 0; i < b; i++)
    global_histogram[i] = 0;

  // Inicializar global histogram con valores de la imagen
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = item->getValueImage(row,col);
      global_histogram[v]++;
    }
  }

  int q1_global = statistic_order(global_histogram, nc, b, 0.25);
  int q3_global = statistic_order(global_histogram, nc, b, 0.75);
  double inter_Q_global = q3_global - q1_global;

  // Create local histogram
  int **local_histogram = new int *[H];
  for(int row = 0; row < H; row++)
    local_histogram[row] = new int[b];

  // inicializar local histogram con 0
  for (int row = 0; row < H; row++){
    for (int col = 0; col < b; col++){
      local_histogram[row][col] = 0;
    }
  }

  // inicializar local histogram con valores inicales
  int icol = 0;
  for (int row = 0; row < H; row++){
    for (int i = -radius; i <= radius; i++){
      for (int j = -radius; j <= radius; j++){
        if((row+i >= 0)&&((icol+j >= 0)&&(row+i < H)&&(icol+j < W)))
          local_histogram[row][item->getValueImage(row+i,icol+j)]++;
      }
    }
  }

  int count1 = 0;
  int count2 = 0;

  double y;
  // Algoritmo de Huang
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){

      nc = numberCells(item,row,col,radius);

      int q1_local = statistic_order(local_histogram, row, nc, b, 0.25);
      int median = statistic_order(local_histogram, row, nc, b, 0.5);
      int q3_local = statistic_order(local_histogram, row, nc, b, 0.75);
      double inter_Q_local = q3_local - q1_local;

       // Actualizar histograma
      for (int i = -radius; i <= radius; i++){
        // Eliminado de columna
        if((row+i >= 0)&&((col-radius >= 0)&&(row+i < H)&&(col-radius < W)))
          local_histogram[row][item->getValueImage(row+i,col-radius)]--;
        // Agregando de columna
        if((row+i >= 0)&&((col+radius+1 >= 0)&&(row+i < H)&&(col+radius+1 < W)))
          local_histogram[row][item->getValueImage(row+i,col+radius+1)]++;
      }
      y = median + inter_Q_local/(inter_Q_global+inter_Q_local) * (item->getValueImage(row,col)-median);

      if (y < 0)
        count1++;

      if (y > 255)
        count2++;


      aux->setValueImage(row,col,int(y));
    }
  }

  printf("\n\n <0: %d", count1);
  printf("\n\n >255: %d", count2);
  printf("\n\n pro: %f\n\n", ((count1+count2)*100.0)/(H*W));

  return aux;
}


/***********************************************************************************************
 *
 *                                                AUXILIARY
 *
 ***********************************************************************************************/
int MainWindow::numberCells(CapturedImage *item, int row, int col, int dist){
  if (row >= dist && col >= dist && row < item->getHeight()-dist && col < item->getWidth()-dist)
    return (2*dist + 1)*(2*dist + 1);
  else if (row <= dist && col <= dist)
    return (dist+1)*(dist+1) + (dist+1)*col + (dist+1)*row + (row*col);
  else if (row >= item->getHeight()-dist-1 && col >= item->getWidth()-dist-1)
    return (dist+1)*(dist+1) + (dist+1)*(item->getWidth()-1-col) + (dist+1)*(item->getHeight()-1-row) + ((item->getHeight()-1-row)*(item->getWidth()-1-col));
  else if (row <= dist && col >= item->getWidth()-dist-1)
    return (dist+1)*(dist+1) + (dist+1)*(item->getWidth()-1-col) + (dist+1)*row + (row*(item->getWidth()-1-col));
  else if (row >= item->getHeight()-dist-1 && col <= dist)
    return (dist+1)*(dist+1) + (dist+1)*col + (dist+1)*(item->getHeight()-1-row) + ((item->getHeight()-1-row)*col);
  else if (row <= dist && col >= dist && col < item->getWidth()-dist)
    return (dist*2+1)*(dist*2+1) - (dist*2+1)*(dist-row);
  else if (row > dist && col > dist && col < item->getWidth()-dist)
    return (dist*2+1)*(dist*2+1) - ((dist*2+1)* (dist - item->getHeight() +1 + row));
  else if (col <= dist && row >= dist && col < dist)
    return (dist*2+1)*(dist*2+1) - (dist*2+1)*(dist-col);
  else if (col >= item->getWidth()-dist && row > dist && row < item->getHeight()-dist)
    return  (dist*2+1)*(dist*2+1) - ((dist*2+1)* (dist - item->getWidth() + 1 + col));
  else
    return (2*dist + 1)*(2*dist + 1);
}

int MainWindow::statistic_order(int *histogram, int nc, int b, double k){
    int i = 0;
    int suma = 0;
    while ((i < b) && (suma <= k * nc)){
      suma = suma + histogram[i];
      i = i+1;
    }
   return i-1;
}

int MainWindow::statistic_order(int **histogram, int row, int nc, int b, double k){
    int i = 0;
    int suma = 0;
    while ((i < b) && (suma <= k * nc)){
      suma = suma + histogram[row][i];
      i = i+1;
    }
   return i-1;
}

/***********************************************************************************************
 *
 *                                                METRICS
 *
 ***********************************************************************************************/

void MainWindow::getMSSIM( CapturedImage *image1, CapturedImage *image2, Scalar &m_luminance, Scalar &m_contrast, Scalar &m_structure){

    IplImage *im1 = image1->getImage();
    IplImage *im2 = image2->getImage();

    Mat i1 = cvarrToMat(im1);
    Mat i2 = cvarrToMat(im2);

    // Para profundidad de 8 bits
    double C1 = 6.5025;
    double C2 = 58.5225;
    double C3 = C2/2;

    //   INITS
    int d = CV_32F;

    Mat I1, I2;
    i1.convertTo(I1, d);           // cannot calculate on one byte large values
    i2.convertTo(I2, d);

    Mat I2_2   = I2.mul(I2);        // I2^2
    Mat I1_2   = I1.mul(I1);        // I1^2
    Mat I1_I2  = I1.mul(I2);        // I1 * I2

    Mat mu1;   // PRELIMINARY COMPUTING
    Mat mu2;
    GaussianBlur(I1, mu1, Size(11, 11), 1.5);
    GaussianBlur(I2, mu2, Size(11,11), 1.5);

    Mat mu1_2   =   mu1.mul(mu1);
    Mat mu2_2   =   mu2.mul(mu2);
    Mat mu1_mu2 =   mu1.mul(mu2);

    Mat sigma1_2;
    Mat sigma2_2;
    Mat sigma12;
    Mat sigma1;
    Mat sigma2;
    Mat sigma1_sigma2;

    GaussianBlur(I1_2, sigma1_2, Size(11,11), 1.5);
    sigma1_2 -= mu1_2;

    GaussianBlur(I2_2, sigma2_2, Size(11,11), 1.5);
    sigma2_2 -= mu2_2;

    GaussianBlur(I1_I2, sigma12, Size(11,11), 1.5);
    sigma12 -= mu1_mu2;

    sqrt(sigma1_2,sigma1);

    //cout << "M = "<< endl << " "  <<sigma1_2 << endl << endl;
    //    cv::imwrite("/home/sebastian/Escritorio/Lee/Resultados/Parciales/mu1.jpg", mu1);
    //    cv::imwrite("/home/sebastian/Escritorio/Lee/Resultados/Parciales/mu2.jpg", mu2);

    sqrt(sigma2_2,sigma2);
    sigma1_sigma2 =  sigma1.mul(sigma2);

    Mat luminance, contrast, structure;

    divide(2*mu1_mu2 + C1, mu1_2 + mu2_2 + C1 , luminance);
    m_luminance = mean( luminance );

    divide(2*sigma12 + C2, sigma1_2 + sigma2_2 + C2 , contrast);
    m_contrast = mean( contrast );

    divide(sigma12 + C3, sigma1_sigma2 + C3 , structure);
    m_structure = mean( structure );
}

double MainWindow::getPSNR(CapturedImage *image1, CapturedImage *image2){

    IplImage *im1 = image1->getImage();
    IplImage *im2 = image2->getImage();

    Mat I1 = cvarrToMat(im1);
    Mat I2 = cvarrToMat(im2);

    Mat s1;
    absdiff(I1, I2, s1);       // |I1 - I2|
    s1.convertTo(s1, CV_32F);  // cannot make a square on 8 bits
    s1 = s1.mul(s1);           // |I1 - I2|^2

    Scalar s = cv::sum( s1 );         // sum elements per channel

    double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels

    if( sse <= 1e-10) // for small values return zero
        return 0;
    else{
        double  mse = sse / (double(I1.channels()) * double(I1.total()));
        double psnr = 10.0*log10((255*255)/mse);
        return psnr;
    }
}

double MainWindow::getENL(CapturedImage *image){

  int H = image->getHeight();
  int W = image->getWidth();

  double global_mean = 0;
  int v;
  // mean
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = image->getValueImage(row,col);
      global_mean = global_mean + v;
    }
  }
  global_mean = global_mean/(H*W);

  // variance
  double global_square_sum = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = image->getValueImage(row,col);
      global_square_sum = global_square_sum + (v - global_mean)*(v - global_mean);
    }
  }

  double global_variance = global_square_sum/(H*W);

  return (global_mean*global_mean)/global_variance;
}

double MainWindow::getSSI(CapturedImage *I_original, CapturedImage *I_filtrada){

  int H = I_original->getHeight();
  int W = I_original->getWidth();

  double global_mean_O = 0, global_mean_F = 0;
  int v;
  // mean
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = I_original->getValueImage(row,col);
      global_mean_O = global_mean_O + v;

      v = I_filtrada->getValueImage(row,col);
      global_mean_F = global_mean_F + v;
    }
  }
  global_mean_O = global_mean_O/(H*W);
  global_mean_F = global_mean_F/(H*W);

  // variance
  double global_square_sum_O = 0, global_square_sum_F = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = I_original->getValueImage(row,col);
      global_square_sum_O = global_square_sum_O + (v - global_mean_O)*(v - global_mean_O);

      v = I_filtrada->getValueImage(row,col);
      global_square_sum_F = global_square_sum_F + (v - global_mean_F)*(v - global_mean_F);

    }
  }

  double global_variance_O = global_square_sum_O/(H*W);
  double global_variance_F = global_square_sum_F/(H*W);

  return (sqrt(global_variance_F)*global_mean_O)/(sqrt(global_variance_O)*global_mean_F);
}


double MainWindow::getSMPI(CapturedImage *I_original, CapturedImage *I_filtrada){

  int H = I_original->getHeight();
  int W = I_original->getWidth();

  double global_mean_O = 0, global_mean_F = 0;
  int v;
  // mean
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = I_original->getValueImage(row,col);
      global_mean_O = global_mean_O + v;

      v = I_filtrada->getValueImage(row,col);
      global_mean_F = global_mean_F + v;
    }
  }
  global_mean_O = global_mean_O/(H*W);
  global_mean_F = global_mean_F/(H*W);

  double Q = global_mean_O - global_mean_F;

  if (Q < 0)
    Q = -Q;

  Q= Q + 1;   // Q = 1 + |mean_O - Mean_F|

  // variance
  double global_square_sum_O = 0, global_square_sum_F = 0;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      v = I_original->getValueImage(row,col);
      global_square_sum_O = global_square_sum_O + (v - global_mean_O)*(v - global_mean_O);

      v = I_filtrada->getValueImage(row,col);
      global_square_sum_F = global_square_sum_F + (v - global_mean_F)*(v - global_mean_F);

    }
  }

  double global_variance_O = global_square_sum_O/(H*W);
  double global_variance_F = global_square_sum_F/(H*W);

  return Q*(sqrt(global_variance_F)/sqrt(global_variance_O));
}

Scalar MainWindow::getMSSIM( CapturedImage *image1, CapturedImage *image2){

    IplImage *im1 = image1->getImage();
    IplImage *im2 = image2->getImage();

    Mat i1 = cvarrToMat(im1);
    Mat i2 = cvarrToMat(im2);

    // Para profundidad de 8 bits
    const double C1 = 6.5025, C2 = 58.5225;

    int d = CV_32F;

    Mat I1, I2;
    i1.convertTo(I1, d);           // cannot calculate on one byte large values
    i2.convertTo(I2, d);

    Mat I2_2   = I2.mul(I2);        // I2^2
    Mat I1_2   = I1.mul(I1);        // I1^2
    Mat I1_I2  = I1.mul(I2);        // I1 * I2

    Mat mu1;
    Mat mu2;
    GaussianBlur(I1, mu1, Size(11, 11), 1.5);
    GaussianBlur(I2, mu2, Size(11,11), 1.5);

    Mat mu1_2   =   mu1.mul(mu1);
    Mat mu2_2   =   mu2.mul(mu2);
    Mat mu1_mu2 =   mu1.mul(mu2);

    Mat sigma1_2;
    Mat sigma2_2;
    Mat sigma12;

    GaussianBlur(I1_2, sigma1_2, Size(11,11), 1.5);
    sigma1_2 -= mu1_2;

    GaussianBlur(I2_2, sigma2_2, Size(11,11), 1.5);
    sigma2_2 -= mu2_2;

    GaussianBlur(I1_I2, sigma12, Size(11,11), 1.5);
    sigma12 -= mu1_mu2;

    Mat t1, t2, t3;

    t1 = 2 * mu1_mu2 + C1;
    t2 = 2 * sigma12 + C2;
    t3 = t1.mul(t2);              // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

    t1 = mu1_2 + mu2_2 + C1;
    t2 = sigma1_2 + sigma2_2 + C2;
    t1 = t1.mul(t2);               // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))

    Mat ssim_map;
    divide(t3, t1, ssim_map);      // ssim_map =  t3./t1;

    Scalar mssim = mean( ssim_map ); // mssim = average of ssim map

    return mssim;
}

Scalar MainWindow::speackle_reduction_score (CapturedImage *image1, CapturedImage *image2){

    IplImage *im1 = image1->getImage();
    IplImage *im2 = image2->getImage();

    Mat I1 = cvarrToMat(im1);
    Mat I2 = cvarrToMat(im2);

    //float kdata[] = {0, -1, 0, -1, 4, -1, 0, -1, 0};
    float kdata[] = {-1, -1, -1, -1, 8, -1, -1, -1, -1};
    Mat kernel(3,3,CV_32F, kdata);

    Mat n1, n2;
    int ddepth = -1;
    Point anchor = Point( -1, -1 );
    double  delta = 0;

    // n1 y n2 laplacianos
    filter2D(I1, n1, ddepth , kernel, anchor, delta, BORDER_DEFAULT );
    filter2D(I2, n2, ddepth , kernel, anchor, delta, BORDER_DEFAULT );

    Mat diff = n1 - n2;
    Mat multi = diff.mul(diff);
    Scalar suma1 = cv::sum( multi );

    Mat multi2 = n1.mul(n1);
    Scalar suma2 = cv::sum( multi2 );

    return getMSSIM(image1,image2)*(suma1/suma2);
}

double MainWindow::macana_metric(CapturedImage *I, CapturedImage *K){

  int H = I->getHeight();
  int W = K->getWidth();

  double *I_histogram = new double [256];
  double *K_histogram = new double [256];

  for (int b = 0; b < 256; b++){
    I_histogram[b] = 0;
    K_histogram[b] = 0;
  }

  int valueI, valueK;

  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      valueI = I->getValueImage(row, col);
      I_histogram[valueI]++;
      valueK = K->getValueImage(row, col);
      K_histogram[valueK]++;
    }
  }
  double sum = 0, value;
  for (int b = 0; b < 256; b++){
    value = I_histogram[b] - K_histogram[b];
    if (value < 0)
      value = -value;
    sum = sum + value;
  }
  return sum/(H*W);
}

double MainWindow::entropy_metric(CapturedImage *I){
  int H = I->getHeight();
  int W = I->getWidth();

  double *histogram = new double [256];

  for (int b = 0; b < 256; b++)
    histogram[b] = 0;

  int value;
  for (int row = 0; row < H; row++){
    for (int col = 0; col < W; col++){
      value = I->getValueImage(row, col);
      histogram[value]++;
    }
  }
  for (int b = 0; b < 256; b++)
    histogram[b] = histogram[b]/(H*W);

  double sum = 0;
  for (int b = 0; b < 256; b++){
    if (int(histogram[b]) != 0)
       sum = sum + histogram[b]* log(histogram[b]);
  }
  return -sum;
}

void MainWindow::on_actionInput_Images_Path_triggered(){
   // QMessageBox msgBox;
   // msgBox.setText("The document has been modified.");
   // msgBox.exec();

    QString dirname = QFileDialog::getExistingDirectory(
            this,
            tr("Select a Directory"),
            QDir::currentPath() );
        if( !dirname.isNull() )
        {
          //error
        }
}
