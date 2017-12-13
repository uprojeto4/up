const functions = require('firebase-functions');
const admin = require('firebase-admin');
const gcs = require('@google-cloud/storage')();
const spawn = require('child-process-promise').spawn;
const path = require('path');
const os = require('os');
const fs = require('fs');

admin.initializeApp(functions.config().firebase);

exports.sendNewMessageNotification = functions.database.ref('/negotiations/{user_id}/{negotiation_id}/unreadMessagesCounter').onWrite(event => {
	
	// obtem o id do usuario e do anuncio onde ocorreram mudancas na negociacao
	const user_id = event.params.user_id;
	const negotiation_id = event.params.negotiation_id;

	console.log("Preparing to send notification to " + user_id);
    
    if (event.data.val()) {
		
		// obtem demais dados da negociacao
		const negotiationMetadata = admin.database().ref(`/negotiations/${user_id}/${negotiation_id}`).once('value');
	
		return negotiationMetadata.then(negotiationResult => {
			
			// obtem demais dados da negociacao
			const ad_id = negotiationResult.val().adId;
			const lastMessage = negotiationResult.val().lastMessage;
			const lastMessageSenderId = negotiationResult.val().lastMessageSenderId;
			const unreadMessages = negotiationResult.val().unreadMessagesCounter;
			const remoteUserId = negotiationResult.val().remoteUserId;
			const vendorId = negotiationResult.val().vendorId;
			
			let negotiationType = 0;
	
			if (user_id != vendorId) {
				negotiationType = 1;
			}
			
			if (user_id == lastMessageSenderId || unreadMessages == 0) {
				return;
			} else {
				
				// obtem o titulo da negociacao
				const adTitleQuery = admin.database().ref(`/posts/${ad_id}/title`).once('value');
		
				return adTitleQuery.then(adTitleQueryResult => {
		
					const adTitle = adTitleQueryResult.val();
		
					// obtem o nome do usuario que enviou a mensagem
					const messageSenderQuery = admin.database().ref(`/users/${lastMessageSenderId}/nome`).once('value');
					
					return messageSenderQuery.then(userResult => {
						
						const userName  = userResult.val();
		
						// obtem o token do dispositivo associado ao usuario
						const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
			
						return deviceToken.then(result => {
			
							const token_id  = result.val();
							const payload = {
								notification: {
									title: `Nova mensagem em ${adTitle}`,
									body: `${userName}: ${lastMessage}`,
									icon: "default",
									click_action: "br.ufc.quixada.up_TARGET_NOTIFICATION",
									tag: ad_id,
									sound: "default"
								},
								data: {
									adId: ad_id,
									remoteUserId: remoteUserId,
									negotiationType: negotiationType.toString(),
									unreadMessages: unreadMessages.toString(),
									adTitle: adTitle,
									type: 'mensagem'
								}
							};
						
							return admin.messaging().sendToDevice(token_id, payload).then(response => {
								console.log("Notification sent to ", user_id);
							});
			
						});
					
					});
		
				});
			
			};

		});

	}
	
});

exports.sendAvaliacaoVendedorNotification = functions.database.ref('/users/{user_id}/somaAvVendedor').onWrite(event => {
	
	const user_id = event.params.user_id;

	console.log("Preparing to send evaluation notification to " + user_id);
    
    if (event.data.val()) {
		
		const negotiationMetadata = admin.database().ref(`/users/${user_id}`).once('value');
		return negotiationMetadata.then(negotiationResult => {
		
			const ad_id = negotiationResult.val().ultimaAvaliacao;
			const remote_user_id = negotiationResult.val().ultimoAvaliador;

			const remoteUserNameQuery = admin.database().ref(`/users/${remote_user_id}`).once('value');
			return remoteUserNameQuery.then(remoteUserNameQueryResult => {
				
				const remote_user_name = remoteUserNameQueryResult.val().nome;

				const adTitleQuery = admin.database().ref(`/posts/${ad_id}/title`).once('value');
				return adTitleQuery.then(adTitleQueryResult => {
			
					const adTitle = adTitleQueryResult.val();
					const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
					return deviceToken.then(result => {
				
						const token_id  = result.val();
						const payload = {
							notification: {
								title: `Nova avaliação de ${remote_user_name}`,
								body: `Você foi avaliado em ${adTitle}`,
								icon: "default",
								click_action: "br.ufc.quixada.up_TARGET_NOTIFICATION",
								sound: "default"
							},
							data: {
								adId: ad_id,
								adTitle: adTitle,
								remoteUserId: remote_user_id,
								type: 'avaliacao'
							}
						};
						
						return admin.messaging().sendToDevice(token_id, payload).then(response => {
							console.log("Evaluation notification sent to ", user_id);
						});
			
					});
					
				});
			
			});	
		});

	};

});

exports.sendAvaliacaoCompradorNotification = functions.database.ref('/users/{user_id}/somaAvComprador').onWrite(event => {
	
	const user_id = event.params.user_id;

	console.log("Preparing to send evaluation notification to " + user_id);
    
    if (event.data.val()) {
		
		const negotiationMetadata = admin.database().ref(`/users/${user_id}`).once('value');
		return negotiationMetadata.then(negotiationResult => {
		
			const ad_id = negotiationResult.val().ultimaAvaliacao;
			const remote_user_id = negotiationResult.val().ultimoAvaliador;

			const remoteUserNameQuery = admin.database().ref(`/users/${remote_user_id}`).once('value');
			return remoteUserNameQuery.then(remoteUserNameQueryResult => {
				
				const remote_user_name = remoteUserNameQueryResult.val().nome;

				const adTitleQuery = admin.database().ref(`/posts/${ad_id}/title`).once('value');
				return adTitleQuery.then(adTitleQueryResult => {
			
					const adTitle = adTitleQueryResult.val();
					const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
					return deviceToken.then(result => {
				
						const token_id  = result.val();
						const payload = {
							notification: {
								title: `Nova avaliação de ${remote_user_name}`,
								body: `Você foi avaliado em ${adTitle}`,
								icon: "default",
								click_action: "br.ufc.quixada.up_TARGET_NOTIFICATION",
								sound: "default"
							},
							data: {
								adId: ad_id,
								adTitle: adTitle,
								remoteUserId: remote_user_id,
								type: 'avaliacao'
							}
						};
						
						return admin.messaging().sendToDevice(token_id, payload).then(response => {
							console.log("Evaluation notification sent to ", user_id);
						});
			
					});
					
				});
			
			});	
		});

	};

});

exports.generateThumbnail = functions.storage.object().onChange(event => {
	const object = event.data; // The Storage object.
	const fileBucket = object.bucket; // The Storage bucket that contains the file.
	const filePath = object.name; // File path in the bucket.
	console.log("filepath: " + filePath); 
	//testar se o filepath até a barra e ver se corresponde a PostsPictures
	const contentType = object.contentType; // File content type.
	const resourceState = object.resourceState; // The resourceState is 'exists' or 'not_exists' (for file/folder deletions).
	const metageneration = object.metageneration; // Number of times metadata has been generated. New objects have a value of 1.

	if (!contentType.startsWith('image/')) {
		console.log('This is not an image.');
		return;
	}

	const fileName = path.basename(filePath);

	if (fileName.startsWith('thumb_')) {
		console.log('Already a thumbnail.');
		return;
	}

	if (resourceState === 'not_exists') {
		console.log('This is a deletion event.');
		return;
	}

	if (resourceState === 'exists' && metageneration > 1) {
		console.log('This is a metadata change event.');
		return;
	}

	const bucket = gcs.bucket(fileBucket);
	const tempFilePath = path.join(os.tmpdir(), fileName);
	const metadata = { contentType: contentType };
	return bucket.file(filePath).download({
		destination: tempFilePath
	}).then(() => {
		console.log('Image downloaded locally to', tempFilePath);
		// Generate a thumbnail using ImageMagick.
		return spawn('convert', [tempFilePath, '-thumbnail', '180x180', tempFilePath]);
	}).then(() => {
		console.log('Thumbnail created at', tempFilePath);
		// We add a 'thumb_' prefix to thumbnails file name. That's where we'll upload the thumbnail.
		const thumbFileName = `thumb_${fileName}`;
		const thumbFilePath = path.join(path.dirname(filePath), thumbFileName);
		// Uploading the thumbnail.
		return bucket.upload(tempFilePath, { destination: thumbFilePath, metadata: metadata });
		// Once the thumbnail has been uploaded delete the local file to free up disk space.
	}).then(() => fs.unlinkSync(tempFilePath));
});