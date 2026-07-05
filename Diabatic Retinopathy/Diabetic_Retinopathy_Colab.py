# %% [markdown]
# # Diabetic Retinopathy Classification using MobileNetV2
# **Author:** AntiGravity AI
# This project is designed specifically for execution in Google Colab.
# It features automated Google Drive integration, a robust data pipeline, 
# transfer learning via MobileNetV2, smart model loading, and an advanced prediction system.

# %%
# ==========================================
# 1. SETUP AND IMPORTS
# ==========================================
import os
import cv2
import numpy as np
import pandas as pd
import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense, Dropout
from tensorflow.keras.models import Model, load_model
from tensorflow.keras.utils import to_categorical
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
import datetime
import shutil

# Check if running in Google Colab and handle Drive mounting
try:
    from google.colab import drive
    COLAB = True
    print("Detected Google Colab environment.")
except ImportError:
    COLAB = False
    print("Not running in Google Colab. Operating in local mode.")

# %%
# ==========================================
# 2. CONFIGURATION & DIRECTORY MANAGEMENT
# ==========================================

# Mount Drive and set base directories
if COLAB:
    print("Mounting Google Drive...")
    drive.mount('/content/drive')
    BASE_DIR = '/content/drive/MyDrive/AntiGravity_AI'
else:
    BASE_DIR = './AntiGravity_AI'

# Define architecture specific directories
MODEL_DIR = os.path.join(BASE_DIR, 'model')
RESULTS_DIR = os.path.join(BASE_DIR, 'results')
LOGS_DIR = os.path.join(BASE_DIR, 'logs')

# Safely create all necessary directories
for init_dir in [MODEL_DIR, RESULTS_DIR, LOGS_DIR]:
    os.makedirs(init_dir, exist_ok=True)
    
print(f"Directories initialized at: {BASE_DIR}")

# File Output Paths
MODEL_PATH = os.path.join(MODEL_DIR, 'retinopathy_mobilenetv2.keras')
RESULTS_CSV = os.path.join(RESULTS_DIR, 'predictions_log.csv')

# --- HYPERPARAMETERS ---
IMG_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 5
NUM_CLASSES = 5
SAMPLE_SIZE = 1000  # Limits the dataset size for faster training/demo. Set to None to use full dataset.

# --- DATASET PATHS ---
# Important: Update these paths based on where your dataset is uploaded or extracted in Colab
DATA_CSV = '/content/dataset/train.csv'          # Must contain 'id_code' and 'diagnosis'
TRAIN_IMAGES_DIR = '/content/dataset/train_images'

# %%
# ==========================================
# 3. ROBUST DATA PROCESSING PIPELINE
# ==========================================

def load_and_preprocess_data(csv_path, img_dir, sample_size=None):
    """
    Safely loads CSV labels, samples data, and preprocesses images using OpenCV.
    Includes safeguards against missing/corrupted files.
    """
    print(f"Starting data loading from: {csv_path}")
    if not os.path.exists(csv_path):
        print(f"[ERROR] CSV file not found at {csv_path}")
        print("-> Please extract your dataset and update completely DATA_CSV & TRAIN_IMAGES_DIR.")
        return None, None
        
    df = pd.read_csv(csv_path)
    
    if sample_size and sample_size < len(df):
        print(f"Sampling {sample_size} records out of {len(df)} total available in CSV.")
        df = df.sample(n=sample_size, random_state=42).reset_index(drop=True)
        
    images = []
    labels = []
    
    print("Processing images through OpenCV pipeline...")
    valid_count = 0
    error_count = 0
    
    for _, row in df.iterrows():
        img_id = str(row['id_code'])
        label = row['diagnosis']
        
        # Test for possible file extensions
        img_path = os.path.join(img_dir, f"{img_id}.png")
        if not os.path.exists(img_path):
            img_path = os.path.join(img_dir, f"{img_id}.jpeg")
            if not os.path.exists(img_path):
                img_path = os.path.join(img_dir, f"{img_id}.jpg")
                
        if not os.path.exists(img_path):
            error_count += 1
            continue
            
        try:
            # OpenCV loads as BGR
            img = cv2.imread(img_path)
            if img is None:
                error_count += 1
                continue
                
            # Convert BGR to RGB (Important for pre-trained ImageNet weights)
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
            
            # Resize
            img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
            
            # Use MobileNetV2's built-in robust preprocessing function
            img = preprocess_input(img.astype('float32'))
            
            images.append(img)
            labels.append(label)
            valid_count += 1
            
            if valid_count % 100 == 0:
                print(f"  -> Processed {valid_count} images successfully...")
                
        except Exception as e:
            error_count += 1
            pass
            
    print(f"Data loading complete. Valid: {valid_count} | Errors/Missing: {error_count}")
    
    if valid_count == 0:
        return None, None
        
    X = np.array(images)
    y = to_categorical(np.array(labels), num_classes=NUM_CLASSES)
    
    return X, y

# %%
# ==========================================
# 4. MODEL ARCHITECTURE & SMART LOADING CATCHER
# ==========================================

def construct_model():
    """Compiles a transfer learning architecture based on MobileNetV2."""
    print("Constructing Custom MobileNetV2 architecture...")
    base_model = MobileNetV2(
        weights='imagenet', 
        include_top=False, 
        input_shape=(IMG_SIZE, IMG_SIZE, 3)
    )
    
    # Freeze the foundational base layers
    base_model.trainable = False
    
    # Add customizable top logic for our 5-class problem
    x = base_model.output
    x = GlobalAveragePooling2D(name='avg_pool')(x)
    x = Dense(256, activation='relu', name='dense_relu')(x)
    x = Dropout(0.5, name='dropout_regularization')(x)
    predictions = Dense(NUM_CLASSES, activation='softmax', name='prediction_layer')(x)
    
    model = Model(inputs=base_model.input, outputs=predictions)
    
    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )
    
    return model

def acquire_model(X_train, y_train, X_val, y_val):
    """
    The Smart Model Handler: Loads the model if available to save resources, 
    otherwise initiates training and saves it permanently to Drive.
    """
    if os.path.exists(MODEL_PATH):
        print(f"\n[INFO] Found pre-existing model at: {MODEL_PATH}")
        print("-> Bypassing training process and loading optimal weights...")
        model = load_model(MODEL_PATH)
        print("Model loaded successfully!")
    else:
        print("\n[INFO] No existing model found. Initiating training process...")
        model = construct_model()
        
        callbacks = [
            tf.keras.callbacks.ModelCheckpoint(
                MODEL_PATH, save_best_only=True, monitor='val_accuracy'
            ),
            tf.keras.callbacks.TensorBoard(log_dir=LOGS_DIR)
        ]
        
        history = model.fit(
            X_train, y_train,
            validation_data=(X_val, y_val),
            batch_size=BATCH_SIZE,
            epochs=EPOCHS,
            callbacks=callbacks,
            verbose=1
        )
        print("\n[SUCCESS] Training optimized and saved directly to Drive.")
        
        # Visualize training journey
        plt.figure(figsize=(14, 5))
        
        plt.subplot(1, 2, 1)
        plt.plot(history.history['accuracy'], label='Training Acc', color='blue')
        plt.plot(history.history['val_accuracy'], label='Validation Acc', color='orange')
        plt.title('Model Accuracy')
        plt.xlabel('Epochs')
        plt.ylabel('Accuracy')
        plt.legend()
        plt.grid(True)
        
        plt.subplot(1, 2, 2)
        plt.plot(history.history['loss'], label='Training Loss', color='blue')
        plt.plot(history.history['val_loss'], label='Validation Loss', color='orange')
        plt.title('Model Loss')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()
        plt.grid(True)
        
        plt.show()
        
    return model

# %%
# ==========================================
# 5. ADVANCED PREDICTION & RESULT LOGGING SYSTEM
# ==========================================

def predict_single_image(model, img_path):
    """Predicts a single image, evaluates confidence, and archives results."""
    if not os.path.exists(img_path):
        print(f"[ERROR] Cannot locate target image: {img_path}")
        return None
        
    try:
        # Strict loading similar to training step
        img_bgr = cv2.imread(img_path)
        img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
        img_resized = cv2.resize(img_rgb, (IMG_SIZE, IMG_SIZE))
        
        # Apply preprocess logic
        img_normalized = preprocess_input(img_resized.astype('float32'))
        input_tensor = np.expand_dims(img_normalized, axis=0)
        
        # Execute prediction
        preds = model.predict(input_tensor, verbose=0)
        pred_class = int(np.argmax(preds[0]))
        confidence = float(np.max(preds[0]))
        
        # Construct Archival Metadata
        timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
        original_name = os.path.basename(img_path)
        new_archival_name = f"{timestamp}_{original_name}"
        save_path = os.path.join(RESULTS_DIR, new_archival_name)
        
        # Execute save operation: 1. Copy image, 2. Write CSV
        shutil.copy2(img_path, save_path)
        
        data_packet = {
            'Timestamp': [timestamp],
            'Original_Name': [original_name],
            'Archival_Name': [new_archival_name],
            'Predicted_Class': [pred_class],
            'Confidence': [f"{confidence * 100:.2f}%"]
        }
        
        df_new = pd.DataFrame(data_packet)
        if os.path.exists(RESULTS_CSV):
            df_new.to_csv(RESULTS_CSV, mode='a', header=False, index=False)
        else:
            df_new.to_csv(RESULTS_CSV, index=False)
            
        print(f"Prediction Complete -> Class: {pred_class} | Conf: {confidence:.2%} | Saved as: {new_archival_name}")
        
        # Present Output Visually
        plt.figure(figsize=(4, 4))
        plt.imshow(img_rgb)
        plt.title(f"Class: {pred_class} (Conf: {confidence:.2%})", fontweight="bold", color="teal")
        plt.axis('off')
        plt.show()
        
        return data_packet
        
    except Exception as e:
        print(f"[ERROR] Prediction pipeline failed for {img_path}. Details: {e}")
        return None

def predict_batch(model, img_paths):
    """Loop handler for executing multiple predictions safely & efficiently."""
    print(f"\n[INFO] Starting batch analysis for {len(img_paths)} inputs...")
    results = []
    for idx, path in enumerate(img_paths):
        print(f"\nProcessing {idx+1}/{len(img_paths)}: {os.path.basename(path)}")
        res = predict_single_image(model, path)
        if res:
            results.append(res)
    print("\n[SUCCESS] Batch analysis completed successfully.")
    return results

# %%
# ==========================================
# 6. DEMO / EXECUTION BLOCK
# ==========================================

if __name__ == "__main__":
    print("-" * 50)
    print(" DIABETIC RETINOPATHY AI - INITIALIZING PROTOCOL ")
    print("-" * 50)
    
    # Block 1: Train / Load Pipeline
    if os.path.exists(DATA_CSV) and os.path.exists(TRAIN_IMAGES_DIR):
        X, y = load_and_preprocess_data(DATA_CSV, TRAIN_IMAGES_DIR, sample_size=SAMPLE_SIZE)
        
        if X is not None:
            # Generate Splits
            X_train, X_val, y_train, y_val = train_test_split(X, y, test_size=0.2, random_state=42)
            print(f"\n[INFO] Dimensions -> Train: {X_train.shape[0]} | Validation: {X_val.shape[0]}")
            
            # Smart Handle Route
            model = acquire_model(X_train, y_train, X_val, y_val)
            
            # Testing the prediction mechanism on temporary samples (simulate production usage)
            print("\n" + "-" * 50)
            print(" ENTERING PRODUCTION PREDICTION DEMONSTRATION ")
            print("-" * 50)
            
            temp_demo_dir = os.path.join(BASE_DIR, 'temp_demo_cache')
            os.makedirs(temp_demo_dir, exist_ok=True)
            demo_paths = []
            
            # Convert internal validation arrays back to valid static image files temporarily
            for i in range(min(3, X_val.shape[0])):
                test_fp = os.path.join(temp_demo_dir, f"demo_val_{i}.png")
                # De-normalize and BGR converter back for cv2.imwrite
                # Note: preprocess_input maps to [-1, 1], we map back to [0, 255] roughly for image visualization export
                img_export = ((X_val[i] + 1.0) / 2.0 * 255.0).astype(np.uint8)
                cv2.imwrite(test_fp, cv2.cvtColor(img_export, cv2.COLOR_RGB2BGR))
                demo_paths.append(test_fp)
                
            predict_batch(model, demo_paths)
            
            # Clean up temporary generation
            shutil.rmtree(temp_demo_dir)
            
    else:
        print("\n[WARNING] Primary Dataset not detected at defined paths.")
        print("Continuing initialization just in case a model is ready for pure prediction...")
        
        if os.path.exists(MODEL_PATH):
            model = load_model(MODEL_PATH)
            print(f"\n[INFO] Model reliably loaded from Drive Archive.")
            print("To analyze an image, execute: predict_single_image(model, '/path/to/retinopathy_scan.jpeg')")
        else:
            print("\n[FAILURE] No model and no data found. Please upload the dataset and adjust paths.")
    
    print("\n=== SYSTEM OPERATION COMPLETE ===")
